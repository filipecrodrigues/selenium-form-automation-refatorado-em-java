package com.automacao;

import com.automacao.model.Aluno;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

/**
 * Classe principal para automação de preenchimento de formulários Google.
 * Utiliza Selenium WebDriver com Java 21.
 */
public class FormAutomation {

    private static final Logger logger = LoggerFactory.getLogger(FormAutomation.class);
    private static final String FORM_URL = "https://docs.google.com/forms/d/e/1FAIpQLSfh9ebeWaWA27bQYKLOt359E9zvgJzvljSD48Px_VV4qP97eQ/viewform?usp=sharing&ouid=103546220084617813983";
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(10);

    private final WebDriver driver;
    private final WebDriverWait wait;

    public FormAutomation() {
        // Configura o WebDriver Manager para gerenciar o ChromeDriver automaticamente
        WebDriverManager.chromedriver().setup();

        // Configura opções do Chrome
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--start-maximized");
        options.addArguments("--disable-blink-features=AutomationControlled");

        this.driver = new ChromeDriver(options);
        this.wait = new WebDriverWait(driver, WAIT_TIMEOUT);

        logger.info("WebDriver inicializado com sucesso");
    }

    /**
     * Processa a lista de alunos, preenchendo e enviando o formulário para cada um.
     */
    public void processarAlunos(List<Aluno> alunos) {
        logger.info("Iniciando processamento de {} alunos", alunos.size());

        for (Aluno aluno : alunos) {
            try {
                processarAluno(aluno);
            } catch (Exception e) {
                logger.error("Erro ao processar aluno: {} - {}", aluno.nome(), aluno.cpf(), e);
            }
        }

        logger.info("Processamento finalizado");
    }

    /**
     * Processa um aluno individual.
     */
    private void processarAluno(Aluno aluno) throws InterruptedException {
        logger.info("Processando aluno: {}", aluno);

        // Navega para o formulário
        driver.get(FORM_URL);
        Thread.sleep(3000); // Aguarda o carregamento completo

        // Encontra os campos de input
        List<WebElement> campos = driver.findElements(By.cssSelector("input[type='text']"));

        if (campos.size() < 2) {
            logger.error("Campos não encontrados para aluno: {} - {}", aluno.nome(), aluno.cpf());
            return;
        }

        // Preenche o nome
        WebElement campoNome = campos.get(0);
        campoNome.clear();
        campoNome.sendKeys(aluno.nome());

        // Preenche o CPF
        WebElement campoCpf = campos.get(1);
        campoCpf.clear();
        campoCpf.sendKeys(aluno.cpf());

        logger.info("Dados preenchidos para: {}", aluno.nome());
        Thread.sleep(2000);

        // Clica no botão Enviar
        enviarFormulario();
        logger.info("Formulário enviado para: {} - {}", aluno.nome(), aluno.cpf());
        Thread.sleep(3000);

        // Tenta clicar em "Enviar outra resposta"
        if (!clicarEnviarOutraResposta()) {
            logger.warn("Botão 'Enviar outra resposta' não encontrado. Encerrando.");
            return;
        }

        Thread.sleep(2000);
    }

    /**
     * Clica no botão Enviar do formulário.
     */
    private void enviarFormulario() {
        WebElement botaoEnviar = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath("//span[text()='Enviar']"))
        );
        botaoEnviar.click();
    }

    /**
     * Clica no link "Enviar outra resposta".
     *
     * @return true se o botão foi encontrado e clicado, false caso contrário
     */
    private boolean clicarEnviarOutraResposta() {
        try {
            WebElement linkOutraResposta = wait.until(
                    ExpectedConditions.elementToBeClickable(By.linkText("Enviar outra resposta"))
            );
            linkOutraResposta.click();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Fecha o WebDriver.
     */
    public void fechar() {
        if (driver != null) {
            driver.quit();
            logger.info("WebDriver encerrado");
        }
    }

    /**
     * Metodo main para executar a automação.
     */
    public static void main(String[] args) {
        // Dados fictícios dos alunos
        List<Aluno> alunos = List.of(
                new Aluno("Ana Paula Souza", "123.456.789-00"),
                new Aluno("Bruno Lima Costa", "234.567.890-11"),
                new Aluno("Carla Mendes Silva", "345.678.901-22")
        );

        FormAutomation automation = null;

        try {
            automation = new FormAutomation();
            automation.processarAlunos(alunos);
        } catch (Exception e) {
            logger.error("Erro durante a execução da automação", e);
        } finally {
            if (automation != null) {
                automation.fechar();
            }
        }
    }
}