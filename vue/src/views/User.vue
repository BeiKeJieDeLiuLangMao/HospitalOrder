<template>
    <div>
      <div>
        <van-cell-group title="用户信息">
          <van-cell
            title="账号"
            :value="$store.getters.getUser.username === undefined ? '请先登录': $store.getters.getUser.username"
            is-link
            @click="clickAccount"/>
          <van-action-sheet
            v-model="showAccountAction"
            :actions="accountActions"
            cancel-text="取消"
            @cancel="showAccountAction = false"
            @select="selectAccountAction"/>
          <van-cell
            title="绑定 QQ"
            is-link
            @click="clickBindQq"
            :value="$store.getters.getUser.username === undefined ? '请先登录': ($store.getters.getUser.bindQq === undefined ? '尚未绑定': $store.getters.getUser.bindQq)"/>
          <van-cell
            title="剩余试用次数"
            :value="$store.getters.getUser.username === undefined ? '请先登录': $store.getters.getUser.leftTime"
            is-link
            @click="clickLeftTime"/>
          <van-cell
            title="下次可用日期"
            :value="$store.getters.getUser.username === undefined ? '请先登录': ($store.getters.getUser.nextUseTime === undefined ? '随时可用': $store.getters.getUser.nextUseTime)"
            is-link
            @click="clickLastUseTime"/>
        </van-cell-group>
      </div>
      <div>
        <van-cell-group title="关于">
          <van-cell title="常见问题" is-link @click="showQuestionAction = true"/>
          <van-action-sheet
            v-model="showQuestionAction"
            :actions="questions"
            cancel-text="取消"
            @cancel="showQuestionAction = false"
            @select="selectQuestionAction"/>
          <van-cell title="打赏作者" is-link @click="payAuthor"/>
        </van-cell-group>
      </div>
    </div>
</template>

<script>
const accountActions = [
  {
    name: '注销账号'
  }
]
const questions = [
  {
    name: '功能介绍',
    answer: '本小程序能够帮助您快速完成抢号流程，增加抢号成功率，但是并不保证一定能抢到。它会自动查询余号，一旦发现余号会自动帮您下单，并且快速呼出支付宝收银台，之后需要您手动验证支付，值得一提的是：如果您支付的太慢，即便下单成功也有可能挂号失败，作者就有这样的惨痛经历！'
  },
  {
    name: '增加使用次数',
    answer: '如果您的孩子在 30 天内急需多次挂号，可以通过"用户须知->关于->常见问题->联系作者"的方式找到作者，作者可能会酌情增加您的使用机会，前提是您要能证明自己不是黄牛！'
  },
  {
    name: '院方投诉',
    answer: '本工具的设计初衷是帮助用户对抗黄牛，它只会自动重复挂号流程，而且也有一些限制（每 30 天才可使用一次）来防止黄牛恶意试用本小程序。但是，如果院方觉得本小程序造成了恶劣影响，作者十分愿意配合院方销毁本工具，院方可以通过"用户须知->关于->常见问题->联系作者"的方式找到作者'
  },
  {
    name: '联系作者',
    answer: '如需联系作者请联系 QQ 群管理员'
  }
]
export default {
  name: 'user',
  data () {
    return {
      accountActions,
      showAccountAction: false,
      questions,
      showQuestionAction: false
    }
  },
  methods: {
    checkGoToHome () {
      if (this.$store.getters.getUser.username === undefined) {
        this.$router.push('/')
        return false
      } else {
        return true
      }
    },
    clickAccount () {
      if (this.checkGoToHome()) {
        this.showAccountAction = true
      }
    },
    selectAccountAction (item, index) {
      if (index === 0) {
        this.$store.dispatch('setUser', {})
        this.$store.dispatch('setUserInfo', {})
        this.$store.dispatch('setMedCards', {})
        localStorage.removeItem('username')
        localStorage.removeItem('password')
        this.$router.push('/')
      }
      this.showAccountAction = false
    },
    clickBindQq () {
      if (this.checkGoToHome()) {
        this.$dialog.alert({
          message: '为防止黄牛恶意使用本工具，每个账号需要绑定 QQ 号后才可以使用，如有需要，请联系管理员进行绑定',
          confirmButtonText: '我知道了'
        })
      }
    },
    clickLeftTime () {
      if (this.checkGoToHome()) {
        this.$dialog.alert({
          message: '为了使用户可以先简单试用一下小程序的效果，每个账号初始会有一次试用机会，成功下单后(无论是否付款)试用结束，继而开启"每隔 30 天才可以使用一次"的限制',
          confirmButtonText: '我知道了'
        })
      }
    },
    clickLastUseTime () {
      if (this.checkGoToHome()) {
        this.$dialog.alert({
          message: '为防止黄牛恶意使用这个小程序，每个账号隔 30 天才可以使用一次，成功下单后(无论是否付款)会消耗本次使用机会',
          confirmButtonText: '我知道了'
        })
      }
    },
    selectQuestionAction (item) {
      this.showQuestionAction = false
      this.$dialog.alert({
        message: item.answer,
        confirmButtonText: '我知道了'
      })
    },
    payAuthor () {
      this.$dialog.confirm({
        message: '本工具仅靠个人业余维护，租赁云服务器也有不小的花费，如果您不介意的话，能否慷慨解囊打赏一下作者，最后，衷心地地祝愿您的孩子早日康复！',
        confirmButtonText: '打赏一下',
        cancelButtonText: '残忍拒绝'
      }).then(() => {
        let data = 'alipayqr://platformapi/startapp?saId=10000007&clientVersion=3.7.0.0718&qrcode=' +
          encodeURI('https://qr.alipay.com/fkx00506jxkfixll6sbpm30') + '%3F_s%3Dweb-other&_t=' + new Date().getTime()
        window.open(data)
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>

</style>
