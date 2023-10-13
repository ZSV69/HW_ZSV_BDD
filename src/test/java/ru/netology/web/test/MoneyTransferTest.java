package ru.netology.web.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.netology.web.data.DataHelper;
import ru.netology.web.page.DashboardPage;
import ru.netology.web.page.LoginPage;

import static com.codeborne.selenide.Selenide.open;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.netology.web.data.DataHelper.*;

class MoneyTransferTest {

  DashboardPage dashboardPage;
  CardInfo firstCardInfo;
  CardInfo secondCardInfo;

  @BeforeEach
  void setup() {
    open("http://localhost:9999");
    var loginPage = new LoginPage();
    var authInfo = DataHelper.getAuthInfo();
    var verificationPage = loginPage.validLogin(authInfo);
    var verificationCode = getVerificationCodeFor();
    dashboardPage = verificationPage.validVerify(verificationCode);
    firstCardInfo = getFirstCardInfo();
    secondCardInfo = getSecondCardInfo();
  }

  @Test
  void shouldTransferFromFirsToSecond() {
    var firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
    var secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
    var amount = generateValidAmount(firstCardBalance);
    var expectedBalanceFirstCard = firstCardBalance - amount;
    var expectedBalanceSecondCard = secondCardBalance + amount;
    var transferPage = dashboardPage.selectCardToTransfer(secondCardInfo);
    dashboardPage = transferPage.makeValidTransfer(String.valueOf(amount), firstCardInfo);
    var actualBalanceFirstCard = dashboardPage.getCardBalance(firstCardInfo);
    var actualBalanceSecondCard = dashboardPage.getCardBalance(secondCardInfo);
    assertEquals(expectedBalanceFirstCard, actualBalanceFirstCard);
    assertEquals(expectedBalanceSecondCard, actualBalanceSecondCard);
  }

  @Test
  void shouldGetErrorMessageIfTransferMoreBalance() {
    var firstCardBalance = dashboardPage.getCardBalance(firstCardInfo);
    var secondCardBalance = dashboardPage.getCardBalance(secondCardInfo);
    var amount = generateInValidAmount(secondCardBalance);
    var transferPage = dashboardPage.selectCardToTransfer(firstCardInfo);
    transferPage.makeTransfer(String.valueOf(amount), secondCardInfo);
    transferPage.findErrorMessage("Перевод не выполнен, сумма перевода превышает остаток на карте списания");
    var actualBalanceFirstCard = dashboardPage.getCardBalance(firstCardInfo);
    var actualBalanceSecondCard = dashboardPage.getCardBalance(secondCardBalance);
    assertEquals(firstCardBalance, actualBalanceFirstCard);
    assertEquals(secondCardBalance, actualBalanceSecondCard);
  }
}

