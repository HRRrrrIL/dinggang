<template>
  <div>
    <el-card style="margin-bottom: 16px">
      <el-form :model="query" inline>
        <el-form-item label="工单编号">
          <el-input v-model="query.orderNo" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="query.status" placeholder="全部" clearable style="width: 120px">
            <el-option label="待分配" :value="1" />
            <el-option label="处理中" :value="2" />
            <el-option label="待客户确认" :value="3" />
            <el-option label="已闭环" :value="4" />
            <el-option label="已驳回" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="success" @click="handleAdd">创建工单</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="orderNo" label="工单编号" width="150" />
        <el-table-column prop="contactName" label="联系人" width="100" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">{{ typeText(row.type) }}</template>
        </el-table-column>
        <el-table-column prop="urgency" label="紧急度" width="90">
          <template #default="{ row }">
            <el-tag :type="urgencyTagType(row.urgency)">{{ urgencyText(row.urgency) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="问题描述" min-width="200" show-overflow-tooltip />
        <el-table-column prop="status" label="状态" width="110">
          <template #default="{ row }">
            <el-tag :type="statusTagType(row.status)">{{ statusText(row.status) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button v-if="row.status === 1" link type="primary" size="small" @click="handleAssign(row)">分配</el-button>
            <el-button v-if="row.status === 2" link type="success" size="small" @click="handleFinish(row)">完成</el-button>
            <el-button v-if="row.status === 3" link type="warning" size="small" @click="handleScore(row)">评分</el-button>
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

    <!-- 创建工单弹窗 -->
    <el-dialog v-model="dialogVisible" title="创建工单" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="客户ID" required>
          <el-input v-model="form.customerId" placeholder="请输入客户ID" />
        </el-form-item>
        <el-form-item label="联系人">
          <el-input v-model="form.contactName" placeholder="联系人姓名" />
        </el-form-item>
        <el-form-item label="工单类型">
          <el-select v-model="form.type" style="width: 100%">
            <el-option label="合同履约问题" :value="1" />
            <el-option label="产品故障" :value="2" />
            <el-option label="服务咨询" :value="3" />
            <el-option label="投诉建议" :value="4" />
            <el-option label="其他" :value="5" />
          </el-select>
        </el-form-item>
        <el-form-item label="紧急度">
          <el-select v-model="form.urgency" style="width: 100%">
            <el-option label="普通(24h)" :value="1" />
            <el-option label="紧急(4h)" :value="2" />
            <el-option label="特急(2h)" :value="3" />
          </el-select>
        </el-form-item>
        <el-form-item label="问题描述" required>
          <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请描述问题（5W1H）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">提交</el-button>
      </template>
    </el-dialog>

    <!-- 分配弹窗 -->
    <el-dialog v-model="assignVisible" title="分配工单" width="400px">
      <el-form label-width="80px">
        <el-form-item label="负责人ID">
          <el-input v-model="assignForm.assigneeId" placeholder="请输入负责人ID" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" @click="doAssign">确定</el-button>
      </template>
    </el-dialog>

    <!-- 满意度评分弹窗 -->
    <el-dialog v-model="scoreVisible" title="满意度评分" width="450px">
      <el-form label-width="80px">
        <el-form-item label="评分">
          <el-rate v-model="scoreForm.score" />
        </el-form-item>
        <el-form-item label="建议">
          <el-input v-model="scoreForm.suggestion" type="textarea" :rows="3" placeholder="请输入您的建议（选填）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="scoreVisible = false">取消</el-button>
        <el-button type="primary" @click="doScore">提交</el-button>
      </template>
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
const query = ref({ page: 1, size: 10, orderNo: '', status: null })

const dialogVisible = ref(false)
const form = ref({ type: 1, urgency: 1 })

const assignVisible = ref(false)
const assignForm = ref({ id: null, assigneeId: null })

const scoreVisible = ref(false)
const scoreForm = ref({ id: null, score: 5, suggestion: '' })

const typeText = (t) => ['', '履约问题', '产品故障', '服务咨询', '投诉建议', '其他'][t] || '-'
const urgencyText = (u) => ['', '普通', '紧急', '特急'][u] || '-'
const urgencyTagType = (u) => ['', 'info', 'warning', 'danger'][u] || ''
const statusText = (s) => ['', '待分配', '处理中', '待客户确认', '已闭环', '已驳回'][s] || '-'
const statusTagType = (s) => ['', 'warning', 'primary', 'info', 'success', 'danger'][s] || ''

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/orders', { params: query.value })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (e) {} finally { loading.value = false }
}

const resetQuery = () => {
  query.value = { page: 1, size: 10, orderNo: '', status: null }
  loadData()
}

const handleAdd = () => {
  form.value = { type: 1, urgency: 1 }
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.value.customerId) { ElMessage.warning('请输入客户ID'); return }
  if (!form.value.description) { ElMessage.warning('请输入问题描述'); return }
  submitLoading.value = true
  try {
    await request.post('/orders', form.value)
    ElMessage.success('创建成功')
    dialogVisible.value = false
    loadData()
  } catch (e) {} finally { submitLoading.value = false }
}

const handleAssign = (row) => {
  assignForm.value = { id: row.id, assigneeId: null }
  assignVisible.value = true
}

const doAssign = async () => {
  await request.post(`/orders/${assignForm.value.id}/assign`, { assigneeId: Number(assignForm.value.assigneeId) })
  ElMessage.success('分配成功')
  assignVisible.value = false
  loadData()
}

const handleFinish = async (row) => {
  await request.post(`/orders/${row.id}/finish`, { resolveDesc: '已处理完成' })
  ElMessage.success('已完成')
  loadData()
}

const handleScore = (row) => {
  scoreForm.value = { id: row.id, score: 5, suggestion: '' }
  scoreVisible.value = true
}

const doScore = async () => {
  await request.post('/satisfaction', {
    orderId: scoreForm.value.id,
    score: scoreForm.value.score,
    suggestion: scoreForm.value.suggestion
  })
  ElMessage.success('评分成功')
  scoreVisible.value = false
  loadData()
}

onMounted(() => { loadData() })
</script>
