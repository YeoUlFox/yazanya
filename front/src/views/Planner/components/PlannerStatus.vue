<template>
  <div>
    <div class="widget-component">
      <div class="widget-component-icon">
        <span><i class="bi bi-trophy-fill"></i></span>
      </div>
      <div class="widget-component-detail">
        <div>다음 티어까지</div>
        <div style="font-size: 0.8em">{{nextTear_restTime}}</div>
      </div>
    </div>

    <!-- modal -->
    <!-- <b-modal id="modal-planner-status" centered title="목표까지">
      <p class="my-4">전체 투두 리스트 achivement에서 긁어오면됨</p>
    </b-modal> -->
  </div>
</template>

<script>
import { onBeforeMount, ref } from '@vue/runtime-core';
import { useStore } from 'vuex';
import rest_user from '@/rest/user';
export default {
  setup() {
    onBeforeMount(async ()=> {
      let restTime = await rest_user.getRestStudyTime(useStore().getters.getUserID);

      if(restTime || restTime == 0)  {
        nextTear_restTime.value = '';
        if(restTime >= 60) {
          nextTear_restTime.value += Math.floor(restTime / 60) + '시간 '
          nextTear_restTime.value += restTime % 60 == 0? '' : restTime % 60 + '분'
        } else {
          nextTear_restTime.value += restTime + '분';
        }
      } else {
        nextTear_restTime.value = '시간 정보 불러오기 실패';
      }
    })

    let nextTear_restTime = ref("?");
    return {
      nextTear_restTime,
    }
  }
};
</script>

<style scoped>
.widget-component {
  position: relative;
  width: 100%;
  height: 100%;
  padding: 16px;
  background: linear-gradient(-30deg, #feb683, #ff8993);
}

.widget-component * {
  font-size: 20pt;
  color: white;
}

.widget-component::before,
.widget-component::after {
  content: '';
  position: absolute;
  top: -5%;
  right: -15%;
  height: 150px;
  width: 150px;
  background-color: rgba(255, 255, 255, 0.2);
  border-radius: 100%;
}

.widget-component::after {
  top: auto;
  bottom: -5%;
  right: -5%;
}

.widget-component-icon {
  margin-bottom: 15px;
  height: 60px;
  width: 60px;
  background-color: rgba(255, 255, 255, 0.4);
  border-radius: 50%;

  display: flex;
  justify-content: center;
  align-items: center;
  box-shadow: 0 0 8px white;
}
</style>
