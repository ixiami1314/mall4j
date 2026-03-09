<template>
  <footer class="footer">
    <div class="container">
      <div class="footer-top">
        <div class="footer-links">
          <div class="link-group">
            <h4>购物指南</h4>
            <a href="javascript:;">购物流程</a>
            <a href="javascript:;">会员介绍</a>
            <a href="javascript:;">积分规则</a>
            <a href="javascript:;">常见问题</a>
          </div>
          <div class="link-group">
            <h4>配送方式</h4>
            <a href="javascript:;">配送范围</a>
            <a href="javascript:;">配送费用</a>
            <a href="javascript:;">配送时效</a>
          </div>
          <div class="link-group">
            <h4>支付方式</h4>
            <a href="javascript:;">在线支付</a>
            <a href="javascript:;">余额支付</a>
          </div>
          <div class="link-group">
            <h4>售后服务</h4>
            <a href="javascript:;">退换货政策</a>
            <a href="javascript:;">退换货流程</a>
            <a href="javascript:;">退款说明</a>
          </div>
          <div class="link-group">
            <h4>关于我们</h4>
            <a href="javascript:;">公司介绍</a>
            <a href="javascript:;">联系我们</a>
            <a href="javascript:;">加入我们</a>
          </div>
        </div>
      </div>
      <div class="footer-bottom">
        <div class="footer-info">
          <div class="contact-item">
            <el-icon><Phone /></el-icon>
            <span>客服热线：400-888-8888</span>
          </div>
          <div class="contact-item">
            <el-icon><Clock /></el-icon>
            <span>服务时间：9:00-22:00</span>
          </div>
          <div class="contact-item">
            <el-icon><Message /></el-icon>
            <span>邮箱：service@mall4w.com</span>
          </div>
        </div>
        <p class="copyright">
          © 2024 mall4w 商城. All rights reserved. |
          <a href="javascript:;">隐私政策</a> |
          <a href="javascript:;">用户协议</a>
        </p>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { Phone, Clock, Message } from '@element-plus/icons-vue'
</script>

<style lang="scss" scoped>
.footer {
  background: linear-gradient(180deg, #2c3e50 0%, #1a252f 100%);
  color: #fff;
  margin-top: auto;
}
.footer-top {
  padding: 40px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}
.footer-links {
  display: flex;
  justify-content: space-between;
}
.link-group {
  h4 {
    font-size: 16px;
    font-weight: 600;
    margin-bottom: 20px;
    color: #fff;
  }
  a {
    display: block;
    color: rgba(255, 255, 255, 0.7);
    font-size: 13px;
    line-height: 2.2;
    transition: color 0.2s;
    &:hover {
      color: #fff;
    }
  }
}
.footer-bottom {
  padding: 24px 0;
  text-align: center;
}
.footer-info {
  display: flex;
  justify-content: center;
  gap: 40px;
  margin-bottom: 20px;
}
.contact-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: rgba(255, 255, 255, 0.8);
  font-size: 13px;
  .el-icon {
    color: #3a86b9;
  }
}
.copyright {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.5);
  a {
    color: rgba(255, 255, 255, 0.5);
    &:hover {
      color: #fff;
    }
  }
}

@media (max-width: 992px) {
  .footer-links {
    flex-wrap: wrap;
    gap: 30px;
  }
  .link-group {
    flex: 0 0 calc(33.33% - 20px);
  }
}

@media (max-width: 576px) {
  .link-group {
    flex: 0 0 calc(50% - 15px);
  }
  .footer-info {
    flex-direction: column;
    gap: 12px;
  }
}
</style>
