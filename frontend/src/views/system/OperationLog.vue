<template>
  <div>
    <el-card style="margin-bottom: 16px">
      <el-form :model="query" inline>
        <el-form-item label="模块">
          <el-input v-model="query.module" placeholder="如：合同" clearable />
        </el-form-item>
        <el-form-item label="操作人">
          <el-input v-model="query.username" placeholder="账号" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadData">查询</el-button>
          <el-button @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <el-card>
      <el-table :data="tableData" v-loading="loading" border>
        <el-table-column prop="createTime" label="时间" width="160" />
        <el-table-column prop="username" label="操作人" width="120" />
        <el-table-column prop="module" label="模块" width="100" />
        <el-table-column prop="action" label="操作" width="100" />
        <el-table-column prop="ip" label="IP" width="130" />
        <el-table-column prop="costMs" label="耗时(ms)" width="100" />
        <el-table-column prop="result" label="结果" width="80">
          <template #default="{ row }">
            <el-tag :type="row.result === 1 ? 'success' : 'danger'" size="small">
              {{ row.result === 1 ? '成功' : '失败' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="detail" label="详情" min-width="200" show-overflow-tooltip />
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import request from '@/api/request'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const query = ref({ page: 1, size: 20, module: '', username: '' })

const loadData = async () => {
  loading.value = true
  try {
    const res = await request.get('/logs', { params: query.value })
    tableData.value = res.data.records
    total.value = res.data.total
  } catch (e) {} finally { loading.value = false }
}

const resetQuery = () => {
  query.value = { page: 1, size: 20, module: '', username: '' }
  loadData()
}

onMounted(() => { loadData() })
</script>
