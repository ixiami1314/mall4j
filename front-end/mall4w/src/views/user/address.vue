<template>
  <div class="address-page">
    <div class="page-header">
      <h3>收货地址</h3>
      <el-button type="primary" @click="showAddDialog">新增地址</el-button>
    </div>
    <div v-if="addressList.length === 0" class="empty-address">
      <el-empty description="暂无收货地址" />
    </div>
    <div v-else class="address-list">
      <div v-for="addr in addressList" :key="addr.addrId" class="address-item">
        <div class="addr-info">
          <p><strong>{{ addr.receiver }}</strong> {{ addr.mobile }}</p>
          <p>{{ addr.province }}{{ addr.city }}{{ addr.area }}{{ addr.addr }}</p>
          <el-tag v-if="addr.commonAddr === 1" type="danger" size="small">默认</el-tag>
        </div>
        <div class="addr-actions">
          <el-button link @click="handleEdit(addr)">编辑</el-button>
          <el-button link @click="handleSetDefault(addr.addrId)" v-if="addr.commonAddr !== 1">设为默认</el-button>
          <el-button link type="danger" @click="handleDelete(addr.addrId)">删除</el-button>
        </div>
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑地址' : '新增地址'" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="收货人" prop="receiver">
          <el-input v-model="form.receiver" />
        </el-form-item>
        <el-form-item label="手机号" prop="mobile">
          <el-input v-model="form.mobile" />
        </el-form-item>
        <el-form-item label="所在地区" prop="province">
          <el-cascader v-model="form.areaArr" :options="areaOptions" style="width: 100%" />
        </el-form-item>
        <el-form-item label="详细地址" prop="addr">
          <el-input v-model="form.addr" type="textarea" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { getAddressList, addAddress, updateAddress, deleteAddress, setDefaultAddress } from '@/api/address'
import { ElMessage, ElMessageBox } from 'element-plus'

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
  receiver: [{ required: true, message: '请输入收货人', trigger: 'blur' }],
  mobile: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  addr: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
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
    ElMessage.success('修改成功')
  } else {
    await addAddress(form)
    ElMessage.success('添加成功')
  }
  dialogVisible.value = false
  fetchAddressList()
}

const handleSetDefault = async (addrId) => {
  await setDefaultAddress(addrId)
  ElMessage.success('设置成功')
  fetchAddressList()
}

const handleDelete = async (addrId) => {
  await ElMessageBox.confirm('确定删除该地址吗？')
  await deleteAddress(addrId)
  ElMessage.success('删除成功')
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
