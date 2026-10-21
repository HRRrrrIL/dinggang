<template>
  <div>
    <!-- 搜索栏 -->
    <el-card style="margin-bottom: 16px">
      <el-form :model="query" inline>
        <el-form-item label="合同编号">
          <el-input v-model="query.contractNo" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="合同名称">
          <el-input v-model="query.contractName" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="草稿" :value="1" />
            <el-option label="审批中" :value="2" />
            <el-option label="已生效" :value="3" />
            <el-option label="已驳回" :value="4" />
            <el-option label="已归档" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="success" @click="handleAdd">新建合同</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card>
      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="contractNo" label="合同编号" width="160" />
        <el-table-column prop="contractName" label="合同名称" min-width="200" />
        <el-table-column prop="contractType" label="类型" width="100">
          <template #default="{ row }">{{ typeText(row.contractType) }}</template>
        </el-table-column>
        <el-table-column prop="signDate" label="签订日期" width="120" />
        <el-table-column prop="endDate" label="到期日期" width="120" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleDetail(row)">详情</el-button>
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button v-if="row.status === 1 || row.status === 4" link type="success" size="small" @click="handleSubmit(row)">提交审批</el-button>
            <el-button v-if="row.status === 2" link type="warning" size="small" @click="handleApprove(row)">审批</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        style="margin-top: 16px; justify-content: flex-end"
        v-model:current-page="query.page"
        v-model:page-size="query.size"
        :total="total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        @size-change="loadData"
        @current-change="loadData"
      />
    </el-card>

    <!-- 新增/编辑弹窗 -->
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑合同' : '新建合同'" width="650px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="合同名称" required>
          <el-input v-model="form.contractName" placeholder="请输入合同名称" />
        </el-form-item>
        <el-form-item label="客户ID" required>
          <el-input v-model="form.customerId" placeholder="请输入客户ID" />
        </el-form-item>
        <el-form-item label="合同类型">
          <el-select v-model="form.contractType" style="width: 100%">
            <el-option label="产品销售合同" value="SALE" />
            <el-option label="采购合同" value="PURCHASE" />
            <el-option label="服务合作合同" value="SERVICE" />
            <el-option label="框架协议" value="FRAME" />
          </el-select>
        </el-form-item>
        <el-form-item label="签订日期">
          <el-date-picker v-model="form.signDate" type="date" style="width: 100%" />
        </el-form-item>
        <el-form-item label="生效日期">
          <el-date-picker v-model="form.startDate" type="date" style="width: 100%" />
        </el-form-item>
        <el-form-item label="到期日期">
          <el-date-picker v-model="form.endDate" type="date" style="width: 100%" />
        </el-form-item>
        <el-form-item label="合同内容">
          <el-input v-model="form.contentJson" type="textarea" :rows="8" placeholder="请输入合同条款内容，支持纯文本/Markdown格式" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitForm" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>

    <!-- 审批弹窗 -->
    <el-dialog v-model="approveVisible" title="合同审批" width="500px">
      <el-descriptions :column="1" border>
        <el-descriptions-item label="合同名称">{{ approveForm.contractName }}</el-descriptions-item>
        <el-descriptions-item label="合同编号">{{ approveForm.contractNo }}</el-descriptions-item>
      </el-descriptions>
      <el-form style="margin-top: 16px">
        <el-form-item label="审批意见">
          <el-input v-model="approveForm.comment" type="textarea" :rows="3" placeholder="请输入审批意见" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="approveVisible = false">取消</el-button>
        <el-button type="danger" @click="doApprove(false)">驳回</el-button>
        <el-button type="success" @click="doApprove(true)">通过</el-button>
      </template>
    </el-dialog>

    <!-- 合同详情/履约节点弹窗 -->
    <el-dialog v-model="detailVisible" title="合同详情" width="750px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="合同编号">{{ detailInfo.contractNo }}</el-descriptions-item>
        <el-descriptions-item label="合同名称">{{ detailInfo.contractName }}</el-descriptions-item>
        <el-descriptions-item label="合同类型">{{ typeText(detailInfo.contractType) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="statusTagType(detailInfo.status)">{{ statusText(detailInfo.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="签订日期">{{ detailInfo.signDate }}</el-descriptions-item>
        <el-descriptions-item label="到期日期">{{ detailInfo.endDate }}</el-descriptions-item>
      </el-descriptions>

      <el-divider content-position="left">履约节点</el-divider>
      <el-table :data="nodeList" border size="small">
        <el-table-column prop="nodeName" label="节点名称" min-width="150" />
        <el-table-column prop="nodeType" label="类型" width="80">
          <template #default="{ row }">{{ ['', '付款', '交付', '其他'][row.nodeType] }}</template>
        </el-table-column>
        <el-table-column prop="dueDate" label="截止日期" width="110" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag size="small" :type="nodeStatusTag(row.status)">{{ nodeStatusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="finishStandard" label="完成标准" min-width="150" show-overflow-tooltip />
      </el-table>

      <el-divider content-position="left">电子签章</el-divider>
      <el-table :data="sealList" border size="small">
        <el-table-column prop="sealUserName" label="签章人" width="120" />
        <el-table-column prop="sealType" label="类型" width="100">
          <template #default="{ row }">{{ ['', '甲方公章', '乙方公章', '法人签字'][row.sealType] }}</template>
        </el-table-column>
        <el-table-column prop="pageNum" label="页码" width="80" />
        <el-table-column prop="posX" label="X(%)" width="80" />
        <el-table-column prop="posY" label="Y(%)" width="80" />
        <el-table-column prop="createTime" label="签章时间" width="160" />
      </el-table>
      <el-button type="primary" size="small" style="margin-top: 12px" @click="handlePreviewPdf">预览PDF</el-button>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api/request'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = ref({ page: 1, size: 10, contractNo: '', contractName: '', status: null })

const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const form = ref({})

const approveVisible = ref(false)
const approveForm = ref({ id: null, contractName: '', contractNo: '', comment: '' })

const detailVisible = ref(false)
const detailInfo = ref({})
const nodeList = ref([])
const sealList = ref([])

const nodeStatusText = (s) => ['待执行', '进行中', '已完成', '已逾期'][s] || '-'
const nodeStatusTag = (s) => ['info', 'primary', 'success', 'danger'][s] || ''

const statusText = (s) => ['', '草稿', '审批中', '已生效', '已驳回', '已归档'][s] || '-'
const statusTagType = (s) => ['', 'info', 'warning', 'success', 'danger', ''][s] || ''
const typeText = (t) => ({ SALE: '销售合同', PURCHASE: '采购合同', SERVICE: '服务合同', FRAME: '框架协议' })[t] || t

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/contracts', { params: query.value })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (e) {} finally { loading.value = false }
}

const resetQuery = () => {
  query.value = { page: 1, size: 10, contractNo: '', contractName: '', status: null }
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  form.value = { contractType: 'SALE' }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  editId.value = row.id
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDetail = async (row) => {
  detailInfo.value = row
  detailVisible.value = true
  // 加载履约节点
  try {
    const res = await request.get(`/contracts/${row.id}/nodes`)
    nodeList.value = res.data || []
  } catch (e) {
    nodeList.value = []
  }
  // 加载签章列表
  try {
    const res = await request.get(`/contracts/${row.id}/seals`)
    sealList.value = res.data || []
  } catch (e) {
    sealList.value = []
  }
}

const handlePreviewPdf = () => {
  // 简化版：新窗口打开PDF预览页面
  window.open(`/api/contracts/${detailInfo.value.id}/pdf/preview`, '_blank')
}

const handleSubmitForm = async () => {
  if (!form.value.contractName) { ElMessage.warning('请输入合同名称'); return }
  if (!form.value.customerId) { ElMessage.warning('请输入客户ID'); return }
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await request.put(`/contracts/${editId.value}`, form.value)
      ElMessage.success('更新成功')
    } else {
      await request.post('/contracts', form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {} finally { submitLoading.value = false }
}

const handleSubmit = async (row) => {
  await request.post(`/contracts/${row.id}/submit`)
  ElMessage.success('已提交审批')
  loadData()
}

const handleApprove = (row) => {
  approveForm.value = { id: row.id, contractName: row.contractName, contractNo: row.contractNo, comment: '' }
  approveVisible.value = true
}

const doApprove = async (pass) => {
  await request.post(`/contracts/${approveForm.value.id}/approve`, {
    pass: pass,
    comment: approveForm.value.comment
  })
  ElMessage.success(pass ? '审批通过' : '已驳回')
  approveVisible.value = false
  loadData()
}

onMounted(() => { loadData() })
</script>
