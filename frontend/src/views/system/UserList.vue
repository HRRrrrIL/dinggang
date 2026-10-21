<template>
  <div>
    <el-card style="margin-bottom: 16px">
      <el-form :model="query" inline>
        <el-form-item label="账号">
          <el-input v-model="query.username" placeholder="请输入" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button type="success" @click="handleAdd">新增用户</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="账号" width="120" />
        <el-table-column prop="realName" label="姓名" width="120" />
        <el-table-column prop="email" label="邮箱" min-width="180" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="row.status === 1 ? 'success' : 'danger'" size="small">
              {{ row.status === 1 ? '正常' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="160" />
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleAssignRole(row)">分配角色</el-button>
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

    <!-- 新增用户弹窗 -->
    <el-dialog v-model="dialogVisible" title="新增用户" width="450px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="账号" required>
          <el-input v-model="form.username" placeholder="登录账号" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="form.realName" placeholder="真实姓名" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="form.email" placeholder="邮箱" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配角色弹窗 -->
    <el-dialog v-model="roleVisible" title="分配角色" width="400px">
      <el-form label-width="80px">
        <el-form-item label="角色">
          <el-select v-model="roleForm.roleId" style="width: 100%">
            <el-option label="管理员" :value="1" />
            <el-option label="销售" :value="2" />
            <el-option label="法务" :value="3" />
            <el-option label="运维" :value="4" />
            <el-option label="创始人" :value="5" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="roleVisible = false">取消</el-button>
        <el-button type="primary" @click="doAssignRole">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import request from '@/api/request'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = ref({ page: 1, size: 10, username: '' })

const dialogVisible = ref(false)
const form = ref({})

const roleVisible = ref(false)
const roleForm = ref({ userId: null, roleId: null })

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/system/users', { params: query.value })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (e) {} finally { loading.value = false }
}

const handleAdd = () => {
  form.value = {}
  dialogVisible.value = true
}

const handleSubmit = async () => {
  if (!form.value.username) { ElMessage.warning('请输入账号'); return }
  await request.post('/system/users', form.value)
  ElMessage.success('创建成功，默认密码123456')
  dialogVisible.value = false
  loadData()
}

const handleAssignRole = (row) => {
  roleForm.value = { userId: row.id, roleId: null }
  roleVisible.value = true
}

const doAssignRole = async () => {
  await request.post(`/system/users/${roleForm.value.userId}/roles`, { roleId: roleForm.value.roleId })
  ElMessage.success('分配成功')
  roleVisible.value = false
  loadData()
}

onMounted(() => { loadData() })
</script>
