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
  areaArr: [],
  provinceId: null,
  cityId: null,
  areaId: null
})

const rules = {
  receiver: [{ required: true, message: t('user.address.consigneePlaceholder'), trigger: 'blur' }],
  mobile: [{ required: true, message: t('user.address.phonePlaceholder'), trigger: 'blur' }],
  addr: [{ required: true, message: t('user.address.detailPlaceholder'), trigger: 'blur' }]
}

// 级联选择器懒加载配置 - 使用 areaId 作为 value，这样可以正确加载子级
const cascaderProps = {
  lazy: true,
  async lazyLoad(node, resolve) {
    const { level, value } = node
    // 根据 level 确定要请求的 pid
    // level 0: 省份 (pid=0), level 1: 城市 (pid=省areaId), level 2: 区县 (pid=市areaId)
    const pid = level === 0 ? 0 : value

    try {
      const { data } = await getAreaList(pid)
      // 缓存加载的地区数据，用于后续 ID 到名称的转换
      if (data) {
        data.forEach(item => {
          areaMap.value.set(item.areaId, item.areaName)
        })
      }
      const nodes = (data || []).map(item => ({
        value: item.areaId,  // 使用 areaId 作为 value，便于加载子级
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

// 缓存地区数据用于显示名称
const areaMap = ref(new Map())

// 根据 areaId 获取 areaName
const getAreaNameById = (areaId) => {
  return areaMap.value.get(areaId) || ''
}

// 加载地区数据到缓存
const loadAreaToCache = async (pid, level) => {
  try {
    const { data } = await getAreaList(pid)
    if (data) {
      data.forEach(item => {
        areaMap.value.set(item.areaId, item.areaName)
      })
    }
    return data
  } catch (e) {
    console.error('加载地区数据失败', e)
    return []
  }
}

onMounted(async () => {
  fetchAddressList()
  // 预加载省份数据到缓存
  await loadAreaToCache(0, 0)
})

const fetchAddressList = async () => {
  const { data } = await getAddressList()
  addressList.value = data || []
}

const showAddDialog = () => {
  isEdit.value = false
  Object.assign(form, { addrId: null, receiver: '', mobile: '', province: '', city: '', area: '', addr: '', areaArr: [], provinceId: null, cityId: null, areaId: null })
  dialogVisible.value = true
}

// 根据 areaName 查找 areaId（用于编辑时回显）
const findAreaIdByName = async (name, pid) => {
  try {
    const { data } = await getAreaList(pid)
    if (data) {
      const found = data.find(item => item.areaName === name)
      return found ? found.areaId : null
    }
  } catch (e) {
    console.error('查找地区ID失败', e)
  }
  return null
}

const handleEdit = async (addr) => {
  isEdit.value = true
  // 编辑时需要将省市区名称转换为 areaId
  const areaArr = []
  try {
    // 查找省份 ID
    if (addr.province) {
      const provinceId = await findAreaIdByName(addr.province, 0)
      if (provinceId) {
        areaArr.push(provinceId)
        areaMap.value.set(provinceId, addr.province)

        // 查找城市 ID
        if (addr.city) {
          const cityId = await findAreaIdByName(addr.city, provinceId)
          if (cityId) {
            areaArr.push(cityId)
            areaMap.value.set(cityId, addr.city)

            // 查找区县 ID
            if (addr.area) {
              const areaId = await findAreaIdByName(addr.area, cityId)
              if (areaId) {
                areaArr.push(areaId)
                areaMap.value.set(areaId, addr.area)
              }
            }
          }
        }
      }
    }
  } catch (e) {
    console.error('编辑地址时查找地区ID失败', e)
  }

  Object.assign(form, { ...addr, areaArr })
  dialogVisible.value = true
}

const handleAreaChange = async (value) => {
  // 当选择地区时，更新 form 中的省市区字段（value 是 areaId 数组）
  // 需要根据 areaId 获取 areaName，同时设置对应的 ID 字段
  if (value && value.length >= 1) {
    form.province = areaMap.value.get(value[0]) || ''
    form.provinceId = value[0]
  }
  if (value && value.length >= 2) {
    form.city = areaMap.value.get(value[1]) || ''
    form.cityId = value[1]
  }
  if (value && value.length >= 3) {
    form.area = areaMap.value.get(value[2]) || ''
    form.areaId = value[2]
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  // 确保省市区字段已设置（将 areaId 转换为 areaName，同时设置 ID 字段）
  if (form.areaArr && form.areaArr.length >= 1) {
    form.province = areaMap.value.get(form.areaArr[0]) || ''
    form.provinceId = form.areaArr[0]
  }
  if (form.areaArr && form.areaArr.length >= 2) {
    form.city = areaMap.value.get(form.areaArr[1]) || ''
    form.cityId = form.areaArr[1]
  }
  if (form.areaArr && form.areaArr.length >= 3) {
    form.area = areaMap.value.get(form.areaArr[2]) || ''
    form.areaId = form.areaArr[2]
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
