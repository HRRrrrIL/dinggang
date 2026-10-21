<template>
  <div>
    <!-- 搜索栏 -->
    <el-card style="margin-bottom: 16px">
      <el-form :model="query" inline>
        <el-form-item label="客户名称">
          <el-input v-model="query.name" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item label="客户层级">
          <el-select v-model="query.level" placeholder="全部" clearable style="width: 120px">
            <el-option v-for="d in levelDict" :key="d.itemValue" :label="d.itemLabel" :value="Number(d.itemValue)" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
          <el-button type="success" @click="handleAdd">新增客户</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card>
      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="customerNo" label="客户编号" width="150" />
        <el-table-column prop="customerName" label="客户名称" min-width="180" />
        <el-table-column prop="level" label="层级" width="80">
          <template #default="{ row }">
            <el-tag :type="levelTagType(row.level)">{{ levelText(row.level) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="industry" label="行业" width="100" />
        <el-table-column prop="intentDegree" label="意向度" width="80" />
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click="handleDelete(row)">删除</el-button>
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
    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑客户' : '新增客户'" width="600px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="客户名称" required>
          <el-input v-model="form.customerName" placeholder="请输入客户名称" />
        </el-form-item>
        <el-form-item label="客户层级">
          <el-select v-model="form.level" style="width: 100%">
            <el-option label="潜在" :value="1" />
            <el-option label="意向" :value="2" />
            <el-option label="成交" :value="3" />
            <el-option label="复购" :value="4" />
          </el-select>
        </el-form-item>
        <el-form-item label="行业">
          <el-input v-model="form.industry" placeholder="如：互联网/制造业/医疗" />
        </el-form-item>
        <el-form-item label="客户来源">
          <el-input v-model="form.source" placeholder="如：展会/转介绍/官网" />
        </el-form-item>
        <el-form-item label="意向度">
          <el-rate v-model="form.intentDegree" />
        </el-form-item>
        <el-form-item label="需求描述">
          <el-input v-model="form.demandDesc" type="textarea" :rows="3" placeholder="客户需求描述" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="form.remark" type="textarea" :rows="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/api/request'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = ref({ page: 1, size: 10, name: '', level: null })

// 字典
const levelDict = ref([])

const dialogVisible = ref(false)
const isEdit = ref(false)
const editId = ref(null)
const form = ref({
  customerName: '',
  level: 1,
  industry: '',
  source: '',
  intentDegree: 3,
  demandDesc: '',
  remark: ''
})

const levelText = (level) => ['', '潜在', '意向', '成交', '复购'][level] || '-'
const levelTagType = (level) => ['', 'info', 'warning', 'success', 'danger'][level] || ''

const loadDict = async () => {
  const { loadDict } = await import('@/api/dict')
  levelDict.value = await loadDict('customer_level')
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/customers', { params: query.value })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (e) {} finally { loading.value = false }
}

const resetQuery = () => {
  query.value = { page: 1, size: 10, name: '', level: null }
  loadData()
}

const handleAdd = () => {
  isEdit.value = false
  form.value = { customerName: '', level: 1, industry: '', source: '', intentDegree: 3, demandDesc: '', remark: '' }
  dialogVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  editId.value = row.id
  form.value = { ...row }
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm(`确定删除客户"${row.customerName}"吗？`, '提示', { type: 'warning' })
  await request.delete(`/customers/${row.id}`)
  ElMessage.success('删除成功')
  loadData()
}

const handleSubmit = async () => {
  if (!form.value.customerName) {
    ElMessage.warning('请输入客户名称')
    return
  }
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await request.put(`/customers/${editId.value}`, form.value)
      ElMessage.success('更新成功')
    } else {
      await request.post('/customers', form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (e) {} finally { submitLoading.value = false }
}

onMounted(() => { loadDict(); loadData() })
</script>
