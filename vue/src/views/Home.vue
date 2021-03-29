<template>
  <div>
    <div v-show="$store.getters.getOrder === undefined">
      <div v-show="$store.getters.getUser.userid === undefined">
        <van-cell-group title="登录">
          <van-field
            v-model="username"
            clearable
            :readonly="$store.getters.getLoginLoading"
            label="账号"
            right-icon="question-o"
            placeholder="请输入官方 App 账号"
            @click-right-icon="$toast('本工具需要使用您的官方 APP 账号，但请放心，账号和密码只会保存在本机')"/>
          <van-field
            v-model="password"
            :readonly="$store.getters.getLoginLoading"
            type="password"
            label="密码"
            placeholder="请输入官方 App 密码"/>
        </van-cell-group>
        <van-button
          type="info"
          :loading="$store.getters.getLoginLoading"
          loading-text="登录中..."
          @click="login"
          :disabled="username.length === 0 || password.length === 0">登录
        </van-button>
      </div>
      <van-skeleton
        v-if="$store.getters.getUser.userid !== undefined"
        :row="20"
        :loading="loading">
        <van-cell-group title="查号选项">
          <van-cell
            title="科室选择"
            :value="$store.getters.getDepartments.length === 0 ? '加载中...': ('当前选择: ' + $store.getters.getDepartments[this.$store.getters.getDepartmentIndex].deptName)"
            :is-link="$store.getters.getDepartments.length !== 0"
            @click="showDepartmentPicker = true"/>
          <van-popup v-model="showDepartmentPicker" position="bottom">
            <van-picker
              show-toolbar
              value-key="deptName"
              :default-index="this.$store.getters.getDepartmentIndex"
              :columns="$store.getters.getDepartments"
              @cancel="showDepartmentPicker = false"
              @confirm="changeDepartment"/>
          </van-popup>
          <van-cell
            title="门诊类型"
            :value="'当前选择: ' + outpatientType[$store.getters.getOutpatientTypeIndex].type"
            is-link
            @click="showTypePicker = true"/>
          <van-popup v-model="showTypePicker" position="bottom">
            <van-picker
              show-toolbar
              value-key="type"
              :default-index="$store.getters.getOutpatientTypeIndex"
              :columns="outpatientType"
              @cancel="showTypePicker = false"
              @confirm="changeOutpatientType"/>
          </van-popup>
          <van-cell
            title="日期选择"
            @click="showDatePicker = true"
            :value="'当前选择: ' + selectedDateStr"
            is-link/>
          <van-popup v-model="showDatePicker" position="bottom">
            <van-datetime-picker
              v-model="datePickerDate"
              type="date"
              :min-date="$store.getters.getToday"
              :formatter="datePickerFormat"
              :max-date="endDay"
              @cancel="showDatePicker = false"
              @confirm="changeDate"/>
          </van-popup>
        </van-cell-group>
        <van-cell-group title="可选号源">
          <van-list v-model="loadingItems" :finished="!loadingItems">
            <van-cell
              v-for="item in $store.getters.getItems"
              v-show="!loadingItems"
              :key="item.specCode"
              :title="item.specName + '（余号:' + item.validNum + '| ¥' + item.medFee + '）'"
              value="开始预约"
              @click="selectRegToken(item)"
              is-link/>
            <h3 v-show="!loadingItems && $store.getters.getItems.length === 0">
              当天无可挂号源
            </h3>
            <van-popup v-model="showRegTokenSelector" position="bottom">
              <van-picker
                show-toolbar
                title="选择预约时间段"
                value-key="schedulingName"
                :columns="regTokens"
                confirm-button-text="下一步"
                @cancel="showRegTokenSelector = false"
                @confirm="doSelectRegToken"/>
            </van-popup>
            <van-popup v-model="showMedCardSelector" position="bottom">
              <van-picker
                show-toolbar
                title="选择就诊卡"
                value-key="name"
                :columns="$store.getters.getMedCards"
                confirm-button-text="下一步"
                @cancel="showMedCardSelector = false"
                @confirm="doSelectMedCard"/>
            </van-popup>
            <van-dialog
              v-model="showTaskConfirm"
              title="请确认预约任务"
              :showCancelButton="submittingTaskCallback === undefined"
              confirm-button-text="确认无误"
              cancel-button-text="重新选择"
              :beforeClose="confirmTask"
              show-cancel-button>
              <van-cell title="科室" :value="selectedItem.deptName" />
              <van-cell title="门诊类型" :value="outpatientType[$store.getters.getOutpatientTypeIndex].type" />
              <van-cell title="日期" :value="selectedDateStr" />
              <van-cell title="号源" :value="selectedItem.specName" />
              <van-cell title="时间" :value="selectedRegTokens.schedulingName" />
              <van-cell title="就诊卡" :value="selectedMedCard.name" />
            </van-dialog>
          </van-list>
        </van-cell-group>
      </van-skeleton>
    </div>
    <div v-if="$store.getters.getOrder !== undefined">
      <van-cell-group title="挂号中">
        <van-cell title="科室" :value="$store.getters.getOrder.deptName" />
        <van-cell title="门诊类型" :value="outpatientType[$store.getters.getOutpatientTypeIndex].type" />
        <van-cell title="日期" :value="$store.getters.getOrder.aptDateStr" />
        <van-cell title="号源" :value="$store.getters.getOrder.regName" />
        <van-cell title="时间" :value="$store.getters.getOrder.time" />
        <van-cell title="就诊卡" :value="$store.getters.getOrder.medCardName" />
        <van-steps :active="$store.getters.getOrder.state" direction="vertical">
          <van-step>提交任务 {{$store.getters.getOrder.submitTime}}</van-step>
          <van-step>
            <div v-show="!$store.getters.getOrder.alreadyShowAptTooLateWarning">
              查询余票 {{'查票:' + $store.getters.getOrder.queryTime + '|余票:' +
              $store.getters.getOrder.validNum + '|下单:' +
              $store.getters.getOrder.tryOrderTime}}
            </div>
            <div v-show="$store.getters.getOrder.alreadyShowAptTooLateWarning">
              {{'将于 ' + $store.getters.getOrder.aptDate + ' 日 0 点开始查询余票'}}
            </div>
          </van-step>
          <van-step>下单成功</van-step>
          <van-step>拉取付款码</van-step>
          <van-step>付款成功</van-step>
        </van-steps>
        <van-button type="danger" @click="cancelTask" v-show="$store.getters.getOrder.state < 3">取消预约</van-button>
        <van-button type="info" @click="alreadyPay" v-show="$store.getters.getOrder.state == 3">已自行付款</van-button>
        <van-button type="primary" @click="deleteOrder" v-show="$store.getters.getOrder.state == 4">恭喜！挂号成功！</van-button>
      </van-cell-group>
    </div>
  </div>
</template>

<script>
const outpatientType = [
  {
    type: '普通门诊',
    regLevel: 0
  },
  {
    type: '特需专家',
    regLevel: 1
  }
]
const moment = require('moment')
export default {
  name: 'home',
  data () {
    return {
      username: '',
      password: '',
      showDepartmentPicker: false,
      outpatientType,
      showTypePicker: false,
      days: [this.$store.getters.getToday,
        this.addDays(this.$store.getters.getToday, 1),
        this.addDays(this.$store.getters.getToday, 2),
        this.addDays(this.$store.getters.getToday, 3),
        this.addDays(this.$store.getters.getToday, 4),
        this.addDays(this.$store.getters.getToday, 5),
        this.addDays(this.$store.getters.getToday, 6),
        this.addDays(this.$store.getters.getToday, 7)
      ],
      endDay: this.addDays(this.$store.getters.getToday, 7),
      datePickerDate: this.$store.getters.getToday,
      showDatePicker: false,
      loadingItems: false,
      showRegTokenSelector: false,
      selectedItem: {},
      regTokens: [],
      selectedRegTokens: {},
      showMedCardSelector: false,
      selectedMedCard: {},
      showTaskConfirm: false,
      submittingTaskCallback: undefined,
      taskRunning: false
    }
  },
  sockets: {
    loadItems: function (items) {
      this.loadingItems = false
    },
    submitTask: function (result) {
      this.submittingTaskCallback()
      this.showTaskConfirm = false
      if (typeof result === 'string') {
        this.$toast.fail(result)
        return
      }
      this.$store.dispatch('setOrder', result)
    },
    cancelTask: function (result) {
      if (typeof result === 'string') {
        this.$toast.fail(result)
        return
      }
      this.$toast.success('已取消任务')
      this.$store.dispatch('setOrder', undefined)
    }
  },
  computed: {
    loading: function () {
      if (this.$store.getters.getInitedCount === 0) {
        if (!this.$store.getters.getLoadedItems) {
          this.loadItems()
        }
        return false
      }
      return true
    },
    selectedDateStr: function () {
      return this.$store.getters.getSelectedDate + this.getDayStr(moment(this.$store.getters.getSelectedDate).day())
    }
  },
  methods: {
    addDays (date, days) {
      const copy = new Date(Number(date))
      copy.setDate(date.getDate() + days)
      return copy
    },
    changeDepartment (value, index) {
      this.$store.dispatch('setDepartmentIndex', index)
      this.showDepartmentPicker = false
      this.loadItems()
    },
    changeOutpatientType (value, index) {
      this.$store.dispatch('setOutpatientTypeIndex', index)
      this.showTypePicker = false
      this.loadItems()
    },
    datePickerFormat (type, value) {
      if (type === 'day') {
        for (let index in this.days) {
          if (this.days.hasOwnProperty(index) && moment(this.days[index]).format('DD') === value) {
            return value + this.getDayStr(moment(this.days[index]).day())
          }
        }
        return value
      } else {
        return value
      }
    },
    getDayStr (index) {
      switch (index) {
        case 1:
          return '(周一)'
        case 2:
          return '(周二)'
        case 3:
          return '(周三)'
        case 4:
          return '(周四)'
        case 5:
          return '(周五)'
        case 6:
          return '(周六)'
        case 0:
          return '(周天)'
      }
    },
    changeDate () {
      this.$store.dispatch('setSelectedDate', moment(this.datePickerDate).format('YYYY-MM-DD'))
      this.showDatePicker = false
      this.loadItems()
    },
    login () {
      this.$store.dispatch('setLoginLoading', true)
      this.$socket.emit('login', {
        username: this.username,
        password: this.password
      })
    },
    loadItems () {
      this.loadingItems = true
      this.$socket.emit('loadItems', {
        date: this.$store.getters.getSelectedDate,
        department: this.$store.getters.getDepartments[this.$store.getters.getDepartmentIndex].deptId,
        regLevel: this.outpatientType[this.$store.getters.getOutpatientTypeIndex].regLevel
      })
    },
    selectRegToken (item) {
      this.selectedItem = JSON.parse(JSON.stringify(item))
      if (Object.values(item.regTokens).length > 1) {
        let regTokens = Object.values(item.regTokens)
        this.regTokens = [{
          regToken: [],
          schedulingName: '全天（按余号数量优先）'
        }]
        for (let index in regTokens) {
          let data = JSON.parse(JSON.stringify(regTokens[index]))
          data.regToken = [data.regToken]
          data.schedulingName = data.schedulingName + '（余号:' + data.validNum + '| ¥' + data.medFee + '）'
          this.regTokens.push(data)
          this.regTokens[0].regToken.push(data.regToken[0])
        }
      } else {
        let data = JSON.parse(JSON.stringify(Object.values(item.regTokens)[0]))
        this.regTokens = [data]
        this.regTokens[0].regToken = [data.regToken]
        this.regTokens[0].schedulingName = data.schedulingName + '（余号:' + data.validNum + '| ¥' + data.medFee + '）'
      }
      this.showRegTokenSelector = true
    },
    doSelectRegToken (regInfo) {
      this.showRegTokenSelector = false
      this.selectedRegTokens = JSON.parse(JSON.stringify(regInfo))
      if (this.$store.getters.getUser.leftTime > 0 ||
        (this.$store.getters.getUser.lastUseTime === undefined ||
          new Date().getTime() > this.addDays(new Date(this.$store.getters.getUser.lastUseTime), 30).getTime())) {
        if (this.$store.getters.getMedCards.length > 0) {
          if (this.$store.getters.getUser.bindQq !== undefined) {
            this.showMedCardSelector = true
          } else {
            this.$dialog.alert({
              message: '您的账号尚未绑定 QQ，请联系管理员进行绑定',
              confirmButtonText: '我知道了'
            })
          }
        } else {
          this.$dialog.alert({
            message: '您的账号尚未绑定就诊卡，请先在官方 APP 中绑定就诊卡后，再回到本工具中进行挂号^.^',
            confirmButtonText: '我知道了'
          })
        }
      } else {
        this.$dialog.alert({
          message: '您的使用机会已耗尽，下次使用时间为 ' +
            moment(this.addDays(new Date(this.$store.getters.getUser.lastUseTime), 30)).format('YYYY-MM-DD HH:mm:ss') +
            ', 如果您短期内急需为孩子多次挂号，请联系管理员^.^',
          confirmButtonText: '我知道了'
        })
      }
    },
    doSelectMedCard (medCard) {
      this.selectedMedCard = medCard
      this.showMedCardSelector = false
      this.doConfirmTask()
    },
    doConfirmTask () {
      this.submittingTaskCallback = undefined
      this.showTaskConfirm = true
    },
    confirmTask (action, done) {
      if (action === 'confirm') {
        this.submittingTaskCallback = done
        this.$socket.emit('submitTask', {
          userId: this.$store.getters.getUser.userid,
          medCardId: this.selectedMedCard.id,
          miBind: this.selectedMedCard.miBind,
          medCardName: this.selectedMedCard.name,
          regLevel: this.outpatientType[this.$store.getters.getOutpatientTypeIndex].regLevel,
          aptDate: this.$store.getters.getSelectedDate,
          aptDateStr: this.$store.getters.getSelectedDate + this.getDayStr(moment(this.$store.getters.getSelectedDate).day()),
          deptName: this.selectedItem.deptName,
          deptId: this.selectedItem.deptId,
          regName: this.selectedItem.specName,
          regTokens: this.selectedRegTokens.regToken,
          time: this.selectedRegTokens.schedulingName,
          submitTime: moment(new Date()).format('YYYY-MM-DD HH:mm:ss'),
          username: this.$store.getters.getUser.username
        })
      } else {
        this.showTaskConfirm = false
        done()
      }
    },
    cancelTask () {
      this.$dialog.confirm({
        title: '提醒',
        message: '您确认要取消预约吗？'
      }).then(() => {
        this.$socket.emit('cancelTask')
      }).catch(() => {})
    },
    alreadyPay () {
      this.$dialog.confirm({
        title: '提醒',
        message: '您确认已经在官方 App 中付款了么？'
      }).then(() => {
        let order = JSON.parse(JSON.stringify(this.$store.getters.getOrder))
        order.state = 4
        this.$store.dispatch('setOrder', order)
      }).catch(() => {})
    },
    deleteOrder () {
      this.$dialog.confirm({
        title: '恭喜',
        message: '您已经挂号成功，本工具仅靠个人业余维护，租赁云服务器也有不小的花费，如果您不介意的话，能否慷慨解囊打赏一下作者，最后，衷心地地祝愿您的孩子早日康复！',
        confirmButtonText: '打赏一下',
        cancelButtonText: '残忍拒绝'
      }).then(() => {
        this.$store.dispatch('setOrder', undefined)
        this.payAuthor()
      }).catch(() => {
        this.$store.dispatch('setOrder', undefined)
      })
    },
    payAuthor () {
      let data = 'alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=' +
        encodeURI('https://qr.alipay.com/fkx00506jxkfixll6sbpm30') + '%3F_s%3Dweb-other&_t=' + new Date().getTime()
      window.open(data)
    }
  }
}
</script>

<style scoped>
</style>
