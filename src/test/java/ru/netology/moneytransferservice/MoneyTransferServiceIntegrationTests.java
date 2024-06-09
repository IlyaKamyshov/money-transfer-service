package ru.netology.moneytransferservice;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import ru.netology.moneytransferservice.model.Amount;
import ru.netology.moneytransferservice.model.ConfirmOperation;
import ru.netology.moneytransferservice.model.Transfer;
import ru.netology.moneytransferservice.response.Response200;
import ru.netology.moneytransferservice.response.ResponseError;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class MoneyTransferServiceIntegrationTests {

	@Autowired
	TestRestTemplate restTemplate;

	@Container
	private static final GenericContainer<?> app = new GenericContainer<>("money-transfer-service-app")
			.withExposedPorts(5500);

	@BeforeAll
	public static void setUp() {
		app.start();
	}

	@Test
	void confirmValid() {

		Transfer validTransfer = new Transfer(
				"1111111111111111",
				"12/25",
				"111",
				"2222222222222222",
				new Amount(10_000L, "RUR"));

		int appPort = app.getMappedPort(5500);

		Response200 transferEntity = restTemplate.postForObject(
				"http://localhost:" + appPort + "/transfer", validTransfer, Response200.class);

		ConfirmOperation confirmOperation = new ConfirmOperation(transferEntity.operationId(), "0000");

		Response200 configrmEntity = restTemplate.postForObject(
				"http://localhost:" + appPort + "/confirmOperation", confirmOperation, Response200.class);

		assertEquals(transferEntity.operationId(), configrmEntity.operationId());

	}

	@Test
	void confirmTransactionExist() {

		String invalidOperationId = "aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa";

		Transfer validTransfer = new Transfer(
				"1111111111111111",
				"12/25",
				"111",
				"2222222222222222",
				new Amount(10_000L, "RUR"));

		int appPort = app.getMappedPort(5500);

		restTemplate.postForObject("http://localhost:" + appPort + "/transfer", validTransfer, Response200.class);

		ConfirmOperation confirmOperation = new ConfirmOperation(invalidOperationId, "0000");

		ResponseError configrmEntity = restTemplate.postForObject(
				"http://localhost:" + appPort + "/confirmOperation", confirmOperation, ResponseError.class);

		assertEquals("Операции " + invalidOperationId + " не существует",
				configrmEntity.message());

	}

	@Test
	void confirmTransferDone() {

		Transfer validTransfer = new Transfer(
				"1111111111111111",
				"12/25",
				"111",
				"2222222222222222",
				new Amount(10_000L, "RUR"));

		int appPort = app.getMappedPort(5500);

		Response200 operationId = restTemplate.postForObject(
				"http://localhost:" + appPort + "/transfer", validTransfer, Response200.class);

		ConfirmOperation confirmOperation = new ConfirmOperation(operationId.operationId(), "0000");

		restTemplate.postForObject(
				"http://localhost:" + appPort + "/confirmOperation", confirmOperation, Response200.class);

		ResponseError configrmEntity = restTemplate.postForObject(
				"http://localhost:" + appPort + "/confirmOperation", confirmOperation, ResponseError.class);

		assertEquals("Операция " + operationId.operationId() + " уже завершена",
				configrmEntity.message());

	}

	@Test
	void confirmTransferWrongSMSCode() {

		Transfer validTransfer = new Transfer(
				"1111111111111111",
				"12/25",
				"111",
				"2222222222222222",
				new Amount(10_000L, "RUR"));

		int appPort = app.getMappedPort(5500);

		Response200 operationId = restTemplate.postForObject(
				"http://localhost:" + appPort + "/transfer", validTransfer, Response200.class);

		ConfirmOperation confirmOperation = new ConfirmOperation(operationId.operationId(), "0001");

		ResponseError configrmEntity = restTemplate.postForObject(
				"http://localhost:" + appPort + "/confirmOperation", confirmOperation, ResponseError.class);

		assertEquals("Неверный ответный код из SMS при подтверждении операции " + operationId.operationId(),
				configrmEntity.message());

	}

	@Test
	void confirmTransferError() {

		Transfer validTransfer = new Transfer(
				"1111111111111111",
				"12/25",
				"111",
				"2222222222222222",
				new Amount(10_000L, "RUR"));

		int appPort = app.getMappedPort(5500);

		Response200 operationId = restTemplate.postForObject(
				"http://localhost:" + appPort + "/transfer", validTransfer, Response200.class);

		ConfirmOperation confirmOperation = new ConfirmOperation(operationId.operationId(), "0001");

		ResponseError configrmEntity = restTemplate.postForObject(
				"http://localhost:" + appPort + "/confirmOperation", confirmOperation, ResponseError.class);

		confirmOperation = new ConfirmOperation(operationId.operationId(), "0000");

		configrmEntity = restTemplate.postForObject(
				"http://localhost:" + appPort + "/confirmOperation", confirmOperation, ResponseError.class);

		assertEquals("При проведении операции " + operationId.operationId() + " произошла ошибка",
				configrmEntity.message());

	}

	@Test
	void cardFromNumber() {

		Transfer invalidTransfer1 = new Transfer(
				"1111",
				"12/25",
				"111",
				"2222222222222222",
				new Amount(10_000L, "RUR"));

		Transfer invalidTransfer2 = new Transfer(
				"abc1111111111111",
				"12/25",
				"111",
				"2222222222222222",
				new Amount(10_000L, "RUR"));

		int appPort = app.getMappedPort(5500);

		ResponseError transferEntity = restTemplate.postForObject("http://localhost:" + appPort + "/transfer",
				invalidTransfer1, ResponseError.class);

		assertEquals("Номер карты должен содержать 16 цифр",
				transferEntity.message());

		transferEntity = restTemplate.postForObject("http://localhost:" + appPort + "/transfer",
				invalidTransfer2, ResponseError.class);

		assertEquals("Номер карты должен содержать 16 цифр",
				transferEntity.message());

	}

	@Test
	void cardFromValidTill() {

		Transfer invalidTransfer1 = new Transfer(
				"1111111111111111",
				"b2/25",
				"111",
				"2222222222222222",
				new Amount(10_000L, "RUR"));

		Transfer invalidTransfer2 = new Transfer(
				"1111111111111111",
				"12/22",
				"111",
				"2222222222222222",
				new Amount(10_000L, "RUR"));

		int appPort = app.getMappedPort(5500);

		ResponseError transferEntity = restTemplate.postForObject("http://localhost:" + appPort + "/transfer",
				invalidTransfer1, ResponseError.class);

		assertEquals("Срок действия карты должен быть в формате MM/YY",
				transferEntity.message());

		transferEntity = restTemplate.postForObject("http://localhost:" + appPort + "/transfer",
				invalidTransfer2, ResponseError.class);

		assertEquals("Срок действия карты истек",
				transferEntity.message());

	}

	@Test
	void cardFromCVV() {

		Transfer invalidTransfer1 = new Transfer(
				"1111111111111111",
				"12/25",
				"11a",
				"2222222222222222",
				new Amount(10_000L, "RUR"));

		Transfer invalidTransfer2 = new Transfer(
				"1111111111111111",
				"12/25",
				"12345",
				"2222222222222222",
				new Amount(10_000L, "RUR"));

		int appPort = app.getMappedPort(5500);

		ResponseError transferEntity = restTemplate.postForObject("http://localhost:" + appPort + "/transfer",
				invalidTransfer1, ResponseError.class);

		assertEquals("CVV карты должен содержать 3 цифры",
				transferEntity.message());

		transferEntity = restTemplate.postForObject("http://localhost:" + appPort + "/transfer",
				invalidTransfer2, ResponseError.class);

		assertEquals("CVV карты должен содержать 3 цифры",
				transferEntity.message());

	}

	@Test
	void cardToNumber() {

		Transfer invalidTransfer1 = new Transfer(
				"1111111111111111",
				"12/25",
				"111",
				"abc2222222222222",
				new Amount(10_000L, "RUR"));

		Transfer invalidTransfer2 = new Transfer(
				"1111111111111111",
				"12/25",
				"111",
				"2222",
				new Amount(10_000L, "RUR"));

		int appPort = app.getMappedPort(5500);

		ResponseError transferEntity = restTemplate.postForObject("http://localhost:" + appPort + "/transfer",
				invalidTransfer1, ResponseError.class);

		assertEquals("Номер карты должен содержать 16 цифр",
				transferEntity.message());

		transferEntity = restTemplate.postForObject("http://localhost:" + appPort + "/transfer",
				invalidTransfer2, ResponseError.class);

		assertEquals("Номер карты должен содержать 16 цифр",
				transferEntity.message());

	}

	@Test
	void Amount() {

		Transfer invalidTransfer1 = new Transfer(
				"1111111111111111",
				"12/25",
				"111",
				"2222222222222222",
				new Amount(99L, "RUR"));

		Transfer invalidTransfer2 = new Transfer(
				"1111111111111111",
				"12/25",
				"111",
				"2222222222222222",
				new Amount(10_000L, "RUS"));

		int appPort = app.getMappedPort(5500);

		ResponseError transferEntity = restTemplate.postForObject("http://localhost:" + appPort + "/transfer",
				invalidTransfer1, ResponseError.class);

		assertEquals("Сумма перевода должна быть не меньше 1 RUR/EUR/USD",
				transferEntity.message());

		transferEntity = restTemplate.postForObject("http://localhost:" + appPort + "/transfer",
				invalidTransfer2, ResponseError.class);

		assertEquals("Допустимые для перевода валюты: RUR, EUR и USD",
				transferEntity.message());

	}

}
