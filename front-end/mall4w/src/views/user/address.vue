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
          <el-cascader v-model="form.areaArr" :options="areaOptions" style="width: 100%" />
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
import { getAddressList, addAddress, updateAddress, deleteAddress, setDefaultAddress } from '@/api/address'
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

const areaOptions = [] // 可接入实际省市区数据

onMounted(() => {
  fetchAddressList()
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
  Object.assign(form, addr)
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

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
