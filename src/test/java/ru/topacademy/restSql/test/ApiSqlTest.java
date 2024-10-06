package ru.topacademy.restSql.test;

import org.junit.jupiter.api.Test;
import ru.topacademy.restSql.data.ApiHelper;
import ru.topacademy.restSql.data.DataHelper;
import ru.topacademy.restSql.data.SqlHelper;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApiSqlTest {

    @Test
    public void validTransferFromFirstToSecond() {
        var authInfo = DataHelper.getAuthInfoWithTestData();
        ApiHelper.makeQueryToLogin(authInfo, 200);
        var verificationCode = SqlHelper.getVerificationCode();
        var verificationInfo = new DataHelper.VerificationInfo(authInfo.getLogin(), verificationCode.getCoda());
        var tokenInfo = ApiHelper.sendQueryToVerify(verificationInfo, 200);
        var cardsBalances = ApiHelper.sendQueryToGetCardsBalance(tokenInfo.getToken(), 200);
        var firstCardBalance = cardsBalances.get(DataHelper.getFirstCardInfo().getId());
        var secondCardBalance = cardsBalances.get(DataHelper.getSecondCardInfo().getId());
        var amount = DataHelper.generateValidAmount(firstCardBalance);
        var transferInfo = new ApiHelper.APITransferInfo(DataHelper.getFirstCardInfo().getNumber(),
                DataHelper.getSecondCardInfo().getNumber(), amount);
        ApiHelper.generateQueryToTransfer(tokenInfo.getToken(), transferInfo, 200);
        cardsBalances = ApiHelper.sendQueryToGetCardsBalance(tokenInfo.getToken(), 200);
        var actualFirstCardBalance = cardsBalances.get(DataHelper.getFirstCardInfo().getId());
        var actualSecondCardBalance = cardsBalances.get(DataHelper.getSecondCardInfo().getId());
        assertAll(( -> assertEquals(firstCardBalance - amount, actualFirstCardBalance),
                () -> assertEquals(secondCardBalance + amount, actualSecondCardBalance));
    }
}
