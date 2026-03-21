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
  background: #0F172A;
  color: #fff;
  margin-top: auto;
}

.footer-top {
  padding: 36px 0 28px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.footer-links {
  display: flex;
  justify-content: space-between;
  gap: 16px;
}

.link-group {
  h4 {
    font-size: 12px;
    font-weight: 600;
    margin-bottom: 14px;
    color: rgba(255, 255, 255, 0.9);
    font-family: 'Outfit', 'Noto Sans SC', sans-serif;
    letter-spacing: 0.02em;
    text-transform: uppercase;
  }

  a {
    display: block;
    color: rgba(255, 255, 255, 0.5);
    font-size: 12px;
    line-height: 2;
    transition: all 0.2s ease;

    &:hover {
      color: $primary-light;
    }
  }
}

.footer-bottom {
  padding: 20px 0;
  text-align: center;
}

.footer-info {
  display: flex;
  justify-content: center;
  gap: 28px;
  margin-bottom: 16px;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: rgba(255, 255, 255, 0.6);
  font-size: 12px;

  .el-icon {
    color: $primary;
    font-size: 14px;
  }
}

.copyright {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.35);

  a {
    color: rgba(255, 255, 255, 0.35);
    transition: color 0.2s;

    &:hover {
      color: $primary-light;
    }
  }
}

@media (max-width: 992px) {
  .footer-links {
    flex-wrap: wrap;
    gap: 20px;
  }

  .link-group {
    flex: 0 0 calc(33.33% - 14px);
  }
}

@media (max-width: 576px) {
  .link-group {
    flex: 0 0 calc(50% - 10px);
  }

  .footer-info {
    flex-direction: column;
    gap: 8px;
  }
}
</style>
