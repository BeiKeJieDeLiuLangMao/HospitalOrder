<template>
  <div id="app">
    <div v-if="$store.getters.getOs.isAlipay || env === 'development'">
      <router-view/>
      <van-tabbar :route="true">
        <van-tabbar-item
          name="/"
          icon="home-o"
          to="/"
          :dot="$store.getters.getOrder !== undefined">挂号
        </van-tabbar-item>
        <van-tabbar-item name="/user" icon="user-o" to="/user">用户须知</van-tabbar-item>
      </van-tabbar>
    </div>
    <div v-if="!$store.getters.getOs.isAlipay && env === 'production'">
      <h2 style="margin: 30px;">请使用支付宝扫码的方式打开本工具！</h2>
      <van-button type="info" @click="openAliScan">帮我从支付宝打开</van-button>
    </div>
  </div>
</template>
<script>
const moment = require('moment')
export default {
  name: 'user',
  data () {
    return {
      env: process.env.NODE_ENV
    }
  },
  sockets: {
    connect: function () {
      this.autoLogin()
    },
    login: function (result) {
      this.$store.dispatch('setLoginLoading', false)
      if (typeof result === 'string') {
        this.$toast.fail(result)
        localStorage.removeItem('username')
        localStorage.removeItem('password')
        return
      }
      if (result.lastUseTime !== undefined) {
        result.nextUseTime = moment(this.addDays(new Date(result.lastUseTime), 30)).format('YYYY-MM-DD HH:mm:ss')
      }
      this.$store.dispatch('setUser', result)
      localStorage.setItem('username', result.username)
      localStorage.setItem('password', result.password)
      this.init()
    },
    loadDepartments: function (departments) {
      if (typeof departments === 'string') {
        this.$toast.fail(departments)
        return
      }
      this.$store.dispatch('setDepartments', departments)
    },
    loadDoctors: function (doctors) {
      if (typeof doctors === 'string') {
        this.$toast.fail(doctors)
        return
      }
      this.$store.dispatch('setDoctors', doctors)
    },
    loadSpec: function (specs) {
      if (typeof specs === 'string') {
        this.$toast.fail(specs)
        return
      }
      this.$store.dispatch('setSpecs', specs)
    },
    loadAppUserInfo: function (userInfo) {
      if (typeof userInfo === 'string') {
        this.$toast.fail(userInfo)
        return
      }
      this.$store.dispatch('setUserInfo', userInfo)
    },
    loadMedCards: function (medCards) {
      if (typeof medCards === 'string') {
        this.$toast.fail(medCards)
        return
      }
      this.$store.dispatch('setMedCards', medCards)
    },
    loadItems: function (items) {
      if (typeof items === 'string') {
        this.$toast.fail(items)
        return
      }
      for (let index in items) {
        if (items.hasOwnProperty(index)) {
          let validNum = 0
          let medFee
          for (let regIndex in items[index].regTokens) {
            if (items[index].regTokens.hasOwnProperty(regIndex)) {
              validNum += items[index].regTokens[regIndex].validNum
              medFee = items[index].regTokens[regIndex].medFee
            }
          }
          items[index].medFee = medFee
          items[index].validNum = validNum
        }
      }
      this.$store.dispatch('setItems', items)
    },
    loginExpired: function () {
      this.$store.dispatch('setUser', {})
    },
    orderException: function (message) {
      console.log(this.$store.getters.getOrder)
      if (message !== '操作频繁请稍候再试' || this.$store.getters.getOrder === undefined) {
        this.$toast.fail({
          message: message
        })
      }
    },
    orderUpdate: function (order) {
      this.$store.dispatch('setOrder', order)
      if (order.state === 3) {
        this.pay(order)
      }
    },
    orderInfo: function (message) {
      this.$toast.success({
        message: message
      })
    }
  },
  methods: {
    addDays (date, days) {
      const copy = new Date(Number(date))
      copy.setDate(date.getDate() + days)
      return copy
    },
    autoLogin () {
      if (this.$store.getters.getUser.userid === undefined) {
        if (localStorage.getItem('username') !== null && localStorage.getItem('password') !== null) {
          let username = localStorage.getItem('username')
          let password = localStorage.getItem('password')
          this.$store.dispatch('setLoginLoading', true)
          this.$socket.emit('login', { username, password })
        }
      }
    },
    init () {
      if (this.$store.getters.getInitedCount !== 0) {
        this.$store.dispatch('resetInitedCount')
        this.loadDepartments()
        this.loadDoctors()
        this.loadSpec()
        this.loadAppUserInfo()
        this.loadMedCards()
      }
    },
    loadDepartments () {
      this.$socket.emit('loadDepartments')
    },
    loadDoctors () {
      this.$socket.emit('loadDoctors')
    },
    loadSpec () {
      this.$socket.emit('loadSpec')
    },
    loadAppUserInfo () {
      this.$socket.emit('loadAppUserInfo')
    },
    loadMedCards () {
      this.$socket.emit('loadMedCards')
    },
    openAliScan () {
      if (this.$store.getters.getOs.isPhone) {
        window.open('alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=' + window.location.href)
      } else {
        this.$dialog.alert({
          message: '需要您在手机上打开本工具，才能帮你打开支付宝哦^.^',
          confirmButtonText: '我知道了'
        })
      }
    },
    pay (order) {
      window.ap.tradePay({
        orderStr: order.aliPayParams.orderStr
      }, (res) => {
        if (res.resultCode === '9000') {
          order.state = 4
          this.$store.dispatch('setOrder', order)
          this.$dialog.confirm({
            title: '恭喜',
            message: '您已经挂号成功，本工具仅靠个人业余维护，租赁云服务器也有不小的花费，如果您不介意的话，能否慷慨解囊打赏一下作者，最后，衷心地地祝愿您的孩子早日康复！',
            confirmButtonText: '打赏',
            cancelButtonText: '残忍拒绝'
          }).then(() => {
            this.payAuthor()
          }).catch(() => {})
        } else {
          this.$dialog.confirm({
            title: '提醒',
            message: '请尽量在呼出的收银台付款，放号高峰期客户端登录可能会失败，付款过慢可能会导致挂号失败！',
            confirmButtonText: '呼出收银台',
            cancelButtonText: '自行付款'
          }).then(() => {
            this.pay(order)
          }).catch(() => {})
        }
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
<style lang="scss">
#app {
  font-family: 'Avenir', Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  text-align: center;
  color: #2c3e50;
}
</style>
