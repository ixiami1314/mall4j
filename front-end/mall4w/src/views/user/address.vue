<template>
  <div class="address-page">
    <div class="page-header">
      <h3>{{ t('user.address.title') }}</h3>
      <el-button type="primary" @click="showAddDialog">{{ t('user.address.add') }}</el-button>
    </div>
    <div v-if="addressList.length === 0" class="empty-address">
      <el-empty :description="t('user.address.empty')" />
    </div>
    <div v-else class="address-list">
      <div v-for="addr in addressList" :key="addr.addrId" class="address-item">
        <div class="addr-info">
          <p><strong>{{ addr.receiver }}</strong> {{ addr.mobile }}</p>
          <p>{{ addr.province }}{{ addr.city }}{{ addr.area }}{{ addr.addr }}</p>
          <el-tag v-if="addr.commonAddr === 1" type="danger" size="small">{{ t('user.address.default') }}</el-tag>
        </div>
        <div class="addr-actions">
          <el-button link @click="handleEdit(addr)">{{ t('user.address.editBtn') }}</el-button>
          <el-button link @click="handleSetDefault(addr.addrId)" v-if="addr.commonAddr !== 1">{{ t('user.address.setDefault') }}</el-button>
          <el-button link type="danger" @click="handleDelete(addr.addrId)">{{ t('user.address.delete') }}</el-button>
        </div>
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? t('user.address.edit') : t('user.address.add')" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item :label="t('user.address.consignee')" prop="receiver">
          <el-input v-model="form.receiver" />
        </el-form-item>
        <el-form-item :label="t('user.address.phone')" prop="mobile">
          <el-input v-model="form.mobile" />
        </el-form-item>
        <el-form-item :label="t('user.address.area')" prop="province">
          <el-cascader
            v-model="form.areaArr"
            :props="cascaderProps"
            style="width: 100%"
            :placeholder="t('user.address.selectArea')"
            @change="handleAreaChange"
          />
        </el-form-item>
        <el-form-item :label="t('user.address.detail')" prop="addr">
          <el-input v-model="form.addr" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">{{ t('common.cancel') }}</el-button>
        <el-button type="primary" @click="handleSubmit">{{ t('common.confirm') }}</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useI18n } from 'vue-i18n'
import { getAddressList, addAddress, updateAddress, deleteAddress, setDefaultAddress, getAreaList } from '@/api/address'
import { ElMessage, ElMessageBox } from 'element-plus'

const { t } = useI18n()

const addressList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const form = reactive({
  addrId: null,
  receiver: '',
  mobile: '',
  province: '',
  city: '',
  area: '',
  addr: '',
  areaArr: []
})

const rules = {
  receiver: [{ required: true, message: t('user.address.consigneePlaceholder'), trigger: 'blur' }],
  mobile: [{ required: true, message: t('user.address.phonePlaceholder'), trigger: 'blur' }],
  addr: [{ required: true, message: t('user.address.detailPlaceholder'), trigger: 'blur' }]
}

// 级联选择器懒加载配置
const cascaderProps = {
  lazy: true,
  async lazyLoad(node, resolve) {
    const { level, value } = node
    // 根据 level 和 value 确定要请求的 pid
    let pid = 0
    if (level === 1) {
      // 获取城市：需要根据省份名称找到对应的 areaId
      pid = node.root ? 0 : await getAreaIdByName(value, 0)
    } else if (level === 2) {
      // 获取区县：需要根据城市名称找到对应的 areaId
      pid = await getAreaIdByName(value, node.parent?.value || '')
    }

    try {
      const { data } = await getAreaList(pid)
      const nodes = (data || []).map(item => ({
        value: item.areaName,
        label: item.areaName,
        leaf: level >= 2 // 第三级（区县）为叶子节点
      }))
      resolve(nodes)
    } catch (e) {
      console.error('获取地区数据失败', e)
      resolve([])
    }
  }
}

// 根据名称获取地区ID（需要先加载省份数据）
const areaCache = ref(new Map())

const getAreaIdByName = async (name, parentName) => {
  // 这里简化处理，实际应该缓存所有地区数据
  // 由于后端返回的是 Area 对象，包含 areaId
  return areaCache.value.get(`${parentName}-${name}`) || 0
}

// 初始化时加载省份数据到缓存
const loadProvincesToCache = async () => {
  try {
    const { data: provinces } = await getAreaList(0)
    if (provinces) {
      provinces.forEach(p => {
        areaCache.value.set(`-${p.areaName}`, p.areaId)
      })
    }
  } catch (e) {
    console.error('加载省份数据失败', e)
  }
}

onMounted(() => {
  fetchAddressList()
  loadProvincesToCache()
})

const fetchAddressList = async () => {
  const { data } = await getAddressList()
  addressList.value = data || []
}

const showAddDialog = () => {
  isEdit.value = false
  Object.assign(form, { addrId: null, receiver: '', mobile: '', province: '', city: '', area: '', addr: '', areaArr: [] })
  dialogVisible.value = true
}

const handleEdit = (addr) => {
  isEdit.value = true
  // 编辑时设置 areaArr
  const areaArr = []
  if (addr.province) areaArr.push(addr.province)
  if (addr.city) areaArr.push(addr.city)
  if (addr.area) areaArr.push(addr.area)
  Object.assign(form, { ...addr, areaArr })
  dialogVisible.value = true
}

const handleAreaChange = (value) => {
  // 当选择地区时，更新 form 中的省市区字段
  if (value && value.length >= 1) {
    form.province = value[0] || ''
  }
  if (value && value.length >= 2) {
    form.city = value[1] || ''
  }
  if (value && value.length >= 3) {
    form.area = value[2] || ''
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  // 确保省市区字段已设置
  if (form.areaArr && form.areaArr.length >= 1) {
    form.province = form.areaArr[0] || ''
    form.city = form.areaArr[1] || ''
    form.area = form.areaArr[2] || ''
  }

  if (isEdit.value) {
    await updateAddress(form)
    ElMessage.success(t('user.address.modifySuccess'))
  } else {
    await addAddress(form)
    ElMessage.success(t('user.address.addSuccess'))
  }
  dialogVisible.value = false
  fetchAddressList()
}

const handleSetDefault = async (addrId) => {
  await setDefaultAddress(addrId)
  ElMessage.success(t('user.address.setSuccess'))
  fetchAddressList()
}

const handleDelete = async (addrId) => {
  await ElMessageBox.confirm(t('user.address.deleteConfirm'))
  await deleteAddress(addrId)
  ElMessage.success(t('user.address.deleteSuccess'))
  fetchAddressList()
}
</script>

<style lang="scss" scoped>
.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #eee;
}
.address-item {
  display: flex;
  justify-content: space-between;
  padding: 15px;
  border: 1px solid #eee;
  margin-bottom: 10px;
  .addr-info p { margin-bottom: 5px; }
}
</style>
