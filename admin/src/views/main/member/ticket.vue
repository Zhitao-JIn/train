<template>
  <div>
    <a-space style="margin-bottom: 16px">
      <a-input v-model:value="params.passengerName" placeholder="请输入乘客姓名" style="width: 150px" />
      <train-select-view v-model="params.trainCode" width="200px"></train-select-view>
      <a-date-picker v-model:value="params.date" valueFormat="YYYY-MM-DD" placeholder="请选择日期" />
      <a-input v-model:value="params.seatType" placeholder="座位类型" style="width: 150px" />
      <a-button type="primary" @click="handleQuery()">查找</a-button>
    </a-space>

    <a-table
        :dataSource="ticketList"
        :columns="columns"
        :pagination="pagination"
        @change="handleTableChange"
        :loading="loading"
        row-key="id"
    >
      <template #bodyCell="{ column, record }">
        <template v-if="column.dataIndex === 'station'">
          {{ record.start }}<br />
          {{ record.end }}
        </template>

        <template v-else-if="column.dataIndex === 'time'">
          {{ record.startTime }}<br />
          {{ record.endTime }}
        </template>

        <template v-else-if="column.dataIndex === 'seat'">
          {{ record.carriageIndex }}车{{ record.row }}排{{ record.col }}号<br />
          {{ record.seatType }}
        </template>

        <template v-else-if="column.dataIndex === 'operation'">
          <a-button type="link" @click="handleDelete(record.id)">删除</a-button>
        </template>
      </template>
    </a-table>
  </div>
</template>

<script>
import { defineComponent, ref, onMounted } from 'vue';
import { notification } from "ant-design-vue";
import axios from "axios";
import TrainSelectView from "@/components/train-select";

export default defineComponent({
  name: "ticket-list-view",
  components: {
    TrainSelectView,
  },
  setup() {
    const ticketList = ref([]);
    const pagination = ref({
      total: 0,
      current: 1,
      pageSize: 10,
    });
    const loading = ref(false);
    const params = ref({
      passengerName: '',
      trainCode: '',
      date: '',
      seatType: '',
    });

    const columns = [
      { title: '车票ID', dataIndex: 'id', key: 'id' },
      { title: '日期', dataIndex: 'date', key: 'date' },
      { title: '车次', dataIndex: 'trainCode', key: 'trainCode' },
      { title: '车站', dataIndex: 'station' },
      { title: '时间', dataIndex: 'time' },
      { title: '乘客', dataIndex: 'passengerName', key: 'passengerName' },
      { title: '座位', dataIndex: 'seat' },
      { title: '操作', dataIndex: 'operation', key: 'operation' },
    ];

    const handleQuery = (param) => {
      if (!param) {
        param = { page: 1, size: pagination.value.pageSize };
      }
      loading.value = true;

      axios.get("/member/admin/ticket/query-list", {
        params: {
          page: param.page,
          size: param.size,
          passengerName: params.value.passengerName,
          trainCode: params.value.trainCode,
          date: params.value.date,
          seatType: params.value.seatType,
        }
      }).then((response) => {
        loading.value = false;
        let data = response.data;
        if (data.success) {
          ticketList.value = data.content.list;
          pagination.value.current = param.page;
          pagination.value.total = data.content.total;
        } else {
          notification.error({ description: data.message });
        }
      }).catch(() => {
        loading.value = false;
        notification.error({ description: '查询失败' });
      });
    };

    const handleTableChange = (page) => {
      pagination.value.pageSize = page.pageSize;
      handleQuery({
        page: page.current,
        size: page.pageSize
      });
    };

    const handleDelete = (id) => {
      axios.delete("/member/ticket/delete/" + id).then(res => {
        if (res.data.success) {
          notification.success({ description: "删除成功" });
          handleQuery();
        } else {
          notification.error({ description: res.data.message });
        }
      });
    };

    onMounted(() => {
      handleQuery();
    });

    return {
      ticketList,
      pagination,
      columns,
      handleTableChange,
      handleQuery,
      loading,
      params,
      handleDelete
    };
  }
})
</script>