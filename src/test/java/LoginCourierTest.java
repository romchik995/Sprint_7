import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;

public class LoginCourierTest {

    Create create = new Create("Romchik", "1234", "Romchik995");
    Login login = new Login("Romchik", "66666");
    Login loginWithoutPassword = new Login("Romchik", "");
    Login loginWithoutLogin = new Login("", "66666");
    Login loginWithBadPassword = new Login("Romchik", "1234");
    Login loginWithBadLogin = new Login("Romigo", "258753");

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";

        given()
                .header("Content-type", "application/json")
                .body(create)
                .when()
                .post("/api/v1/courier");
    }

    @Test
    @DisplayName("Тест на успешный вход")
    @Description("Проверяется возможность пользователя авторизоваться с корректным логином и паролем")
    public void successfulLoginCourier() {
        given()
                .header("Content-type", "application/json")
                .body(login)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .assertThat()
                .statusCode(200)
                .body("id",notNullValue());
    }

    @Test
    @DisplayName("Тест на вход без пароля")
    @Description("Проверяется возможность пользователя авторизоваться без пароля")
    public void loginWithoutPassword() {
        given()
                .header("Content-type", "application/json")
                .body(loginWithoutPassword)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .assertThat()
                .statusCode(400)
                .body("message",equalTo("Недостаточно данных для входа!"));
    }

    @Test
    @DisplayName("Тест на вход без имени пользователя")
    @Description("Проверяется возможность пользователя авторизоваться без имени пользователя")
    public void loginWithoutLogin() {
        given()
                .header("Content-type", "application/json")
                .body(loginWithoutLogin)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .assertThat()
                .statusCode(400)
                .body("message",equalTo("Недостаточно данных для входа!"));
    }

    @Test
    @DisplayName("Тест на вход с неправильным паролем")
    @Description("Проверяется попытку авторизации с неверным паролем")
    public void loginWithBadPassword() {
        given()
                .header("Content-type", "application/json")
                .body(loginWithBadPassword)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .assertThat()
                .statusCode(404)
                .body("message",equalTo("Учетная запись не найдена!"));
    }

    @Test
    @DisplayName("Тест на вход с неправильным именем пользователя")
    @Description("Проверяется попытку авторизации с несуществующим именем пользователя")
    public void loginWithBadLogin() {
        given()
                .header("Content-type", "application/json")
                .body(loginWithBadLogin)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .assertThat()
                .statusCode(404)
                .body("message",equalTo("Учетная запись не найдена!"));
    }

    @After
    public void loginAndDeleteCourier() {
        IdCourier idCourier = given()
                .header("Content-type", "application/json")
                .body(login)
                .when()
                .post("/api/v1/courier/login")
                .then()
                .extract().body().as(IdCourier.class);

        given()
                .header("Content-type", "application/json")
                .body("{\"name\": \"" + idCourier.getId() + "\"}")
                .when()
                .delete("/api/v1/courier/" + idCourier.getId());
    }
}