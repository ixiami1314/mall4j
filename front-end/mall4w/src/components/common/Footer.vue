<template>
  <footer class="footer">
    <div class="container">
      <div class="footer-top">
        <div class="footer-links">
          <div class="link-group">
            <h4>{{ t('footer.shoppingGuide') }}</h4>
            <a href="javascript:;">{{ t('footer.shoppingProcess') }}</a>
            <a href="javascript:;">{{ t('footer.memberIntro') }}</a>
            <a href="javascript:;">{{ t('footer.pointsRule') }}</a>
            <a href="javascript:;">{{ t('footer.faq') }}</a>
          </div>
          <div class="link-group">
            <h4>{{ t('footer.delivery') }}</h4>
            <a href="javascript:;">{{ t('footer.deliveryRange') }}</a>
            <a href="javascript:;">{{ t('footer.deliveryFee') }}</a>
            <a href="javascript:;">{{ t('footer.deliveryTime') }}</a>
          </div>
          <div class="link-group">
            <h4>{{ t('footer.payment') }}</h4>
            <a href="javascript:;">{{ t('footer.onlinePayment') }}</a>
            <a href="javascript:;">{{ t('footer.balancePayment') }}</a>
          </div>
          <div class="link-group">
            <h4>{{ t('footer.afterSale') }}</h4>
            <a href="javascript:;">{{ t('footer.returnPolicy') }}</a>
            <a href="javascript:;">{{ t('footer.returnProcess') }}</a>
            <a href="javascript:;">{{ t('footer.refundNote') }}</a>
          </div>
          <div class="link-group">
            <h4>{{ t('footer.aboutUs') }}</h4>
            <a href="javascript:;">{{ t('footer.companyIntro') }}</a>
            <a href="javascript:;">{{ t('footer.contactUs') }}</a>
            <a href="javascript:;">{{ t('footer.joinUs') }}</a>
          </div>
        </div>
      </div>
      <div class="footer-bottom">
        <div class="footer-info">
          <div class="contact-item">
            <el-icon><Phone /></el-icon>
            <span>{{ t('footer.serviceHotline', { phone: '400-888-8888' }) }}</span>
          </div>
          <div class="contact-item">
            <el-icon><Clock /></el-icon>
            <span>{{ t('footer.serviceTime', { time: '9:00-22:00' }) }}</span>
          </div>
          <div class="contact-item">
            <el-icon><Message /></el-icon>
            <span>{{ t('footer.email', { email: 'service@mall4w.com' }) }}</span>
          </div>
        </div>
        <p class="copyright">
          {{ t('footer.copyright') }} |
          <a href="javascript:;">{{ t('footer.privacyPolicy') }}</a> |
          <a href="javascript:;">{{ t('footer.userAgreement') }}</a>
        </p>
      </div>
    </div>
  </footer>
</template>

<script setup>
import { Phone, Clock, Message } from '@element-plus/icons-vue'
import { useI18n } from 'vue-i18n'

const { t } = useI18n()
</script>

<style lang="scss" scoped>
// 颜色变量 - 淡蓝色系
$primary: #4A9FD4;
$primary-light: #6BB8E8;

.footer {
  background: linear-gradient(180deg, #1E293B 0%, #0F172A 100%);
  color: #fff;
  margin-top: auto;
}

.footer-top {
  padding: 44px 0 36px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
}

.footer-links {
  display: flex;
  justify-content: space-between;
}

.link-group {
  h4 {
    font-size: 14px;
    font-weight: 600;
    margin-bottom: 18px;
    color: #fff;
    font-family: 'Outfit', 'Noto Sans SC', sans-serif;
    letter-spacing: 0.01em;
  }

  a {
    display: block;
    color: rgba(255, 255, 255, 0.6);
    font-size: 13px;
    line-height: 2.1;
    transition: all 0.2s ease;

    &:hover {
      color: $primary-light;
      transform: translateX(2px);
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
  gap: 36px;
  margin-bottom: 18px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 8px;
  color: rgba(255, 255, 255, 0.7);
  font-size: 13px;

  .el-icon {
    color: $primary;
    font-size: 15px;
  }
}

.copyright {
  font-size: 12px;
  color: rgba(255, 255, 255, 0.4);

  a {
    color: rgba(255, 255, 255, 0.4);
    transition: color 0.2s;

    &:hover {
      color: $primary-light;
    }
  }
}

@media (max-width: 992px) {
  .footer-links {
    flex-wrap: wrap;
    gap: 28px;
  }

  .link-group {
    flex: 0 0 calc(33.33% - 18px);
  }
}

@media (max-width: 576px) {
  .link-group {
    flex: 0 0 calc(50% - 14px);
  }

  .footer-info {
    flex-direction: column;
    gap: 10px;
  }
}
</style>
