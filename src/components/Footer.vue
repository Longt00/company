<template>
  <footer class="footer">
    <div class="container">
      <div class="footer-content">
        <!-- 底部版权和联系方式 -->
        <div>
          <div>
            <p>© 2024 Guangzhou Kaicheng Garment Co., Ltd. All rights reserved.</p>
            <div>
              <p>Tel:0086-15920419867</p>
              <span>|</span>
              <p>E-mail:Canton@c-jeans.com</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </footer>
</template>

<script>

</script>

<style scoped>
.footer {
  background-image: url('../images/background/background-footer.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  color: white;
  width: 100%;
  min-height: 300px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: auto;
  flex-shrink: 0;
}

.container {
  width: 100%;
  margin: 0;
  padding: 0 20px;
}

.footer-content {
  text-align: center;
}

.footer-content > div > div {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.footer-content p {
  margin: 0;
  font-size: 14px;
  line-height: 1.5;
}

.footer-content > div > div > div {
  display: flex;
  align-items: center;
  gap: 15px;
}

.footer-content > div > div > div p {
  font-size: 14px;
}

.footer-content span {
  color: white;
  font-size: 16px;
}

/* */
@media (max-width: 768px) {
  .footer {
    min-height: 150px;
    padding: 20px 0;
  }

  .container {
    padding: 0 15px;
  }

  .footer-content > div > div {
    gap: 10px;
  }

  .footer-content p {
    font-size: 12px;
  }

  .footer-content > div > div > div {
    gap: 10px;
    flex-wrap: wrap;
    justify-content: center;
  }

  .footer-content > div > div > div p {
    font-size: 11px;
  }

  .footer-content span {
    font-size: 12px;
  }
}

@media (max-width: 480px) {
  .footer {
    min-height: 120px;
    padding: 15px 0;
  }

  .footer-content > div > div {
    gap: 8px;
  }

  .footer-content p {
    font-size: 11px;
  }

  .footer-content > div > div > div p {
    font-size: 10px;
  }
}
</style>