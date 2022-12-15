import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;


public class CreateCourierTest {
    Create create = new Create("Romchik", "1234", "Romchik995");
    Create createWithoutLogin = new Create("", "123", "Romchik995");
    Create createWithoutPassword = new Create("Romchik", "", "Romchik995");
    Login login = new Login("Romchik", "1234");

    @Before
    public void setUp() {
        RestAssured.baseURI = "http://qa-scooter.praktikum-services.ru/";
    }

    @Test
    @DisplayName("Проверка возможности создать нового пользователя")
    @Description("Проверяет позитивную попытку создать пользователя")
    public void successfulCreateNewCourier() {
        given()
                .header("Content-type", "application/json")
                .body(create)
                .when()
                .post("/api/v1/courier")
                .then().assertThat()
                .statusCode(201)
                .body("ok", is(true));
    }

    @Test
    @DisplayName("Невозможно создать дубликат курьера")
    @Description("Проверяет невозможность создания курьера с существующим именем пользователя")
    public void errorDuplicateCourier() {
        given()
                .header("Content-type", "application/json")
                .body(create)
                .when()
                .post("/api/v1/courier");
        given()
                .header("Content-type", "application/json")
                .body(create)
                .when()
                .post("/api/v1/courier")
                .then().assertThat()
                .statusCode(409)
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));
    }

    @Test
    @DisplayName("Создание курьера без пароля")
    @Description("Проверяет невозможность создания курьера без поля пароль")
    public void createWithoutLogin() {
        given()
                .header("Content-type", "application/json")
                .body(createWithoutLogin)
                .when()
                .post("/api/v1/courier")
                .then().assertThat()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }

    @Test
    @DisplayName("Создание курьера без имени пользователя")
    @Description("Проверяет невозможность создания курьера без поля имя пользователя")
    public void createWithoutPassword() {
        given()
                .header("Content-type", "application/json")
                .body(createWithoutPassword)
                .when()
                .post("/api/v1/courier")
                .then().assertThat()
                .statusCode(400)
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
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