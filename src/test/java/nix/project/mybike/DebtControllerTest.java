package nix.project.mybike;

import nix.project.mybike.controllers.DebtController;
import nix.project.mybike.models.Bike;
import nix.project.mybike.models.Client;
import nix.project.mybike.models.Debt;
import nix.project.mybike.services.BikesService;
import nix.project.mybike.services.ClientService;
import nix.project.mybike.services.DebtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class DebtControllerTest {

    @Mock
    private DebtService debtService;

    @Mock
    private ClientService clientService;

    @Mock
    private BikesService bikesService;

    @InjectMocks
    private DebtController debtController;

    @Mock
    private Model model;

    @Test
    public void testPay_whenDebtAmountIsGreaterThanPaymentAmount_thenErrorMessageIsDisplayed() {
        int debtId = 123;
        int debtAmount = 500;
        int paymentAmount = 200;
        Debt mockDebt = new Debt();
        mockDebt.setAmount(debtAmount);
        Client mockClient = new Client();
        mockClient.setBikes(Collections.emptyList());
        mockClient.setId(1);
        mockClient.setDebts(new ArrayList<>());
        Bike bike = new Bike();
        bike.setId(1);
        mockDebt.setId(debtId);
        mockDebt.setAmount(debtAmount);
        mockDebt.setOwner(mockClient);
        mockDebt.setBike(bike);
        when(debtService.findOne(debtId)).thenReturn(mockDebt);
        when(clientService.findOne(1)).thenReturn(mockClient);
        when(bikesService.findById(1)).thenReturn(bike);
        when(debtService.getTotalDebtForClientAndBike(mockClient, bike)).thenReturn(debtAmount);
        String viewName = debtController.pay(paymentAmount, mockClient.getId(), bike.getId(), model);

        assertThat(viewName).isEqualTo("clients/show");
        verify(model).addAttribute(eq("client"), any(Client.class));
        verify(model).addAttribute(eq("bikes"), any(List.class));
        verify(model, never()).addAttribute(eq("errorMessage"), anyString());
    }

    @Test
    public void testPay_whenPaymentAmountIsGreaterThanDebtAmount_thenDebtIsPaidAndDebtsAreReturned() {
        int debtId = 123;
        int debtAmount = 500;
        int paymentAmount = 1000;
        Debt mockDebt = new Debt();
        mockDebt.setAmount(debtAmount);
        Client mockClient = new Client();
        mockClient.setBikes(Collections.emptyList());
        mockClient.setId(1);
        mockClient.setDebts(Collections.emptyList());
        Bike bike = new Bike();
        bike.setId(1);
        mockDebt.setId(debtId);
        mockDebt.setAmount(debtAmount);
        mockDebt.setOwner(mockClient);
        mockDebt.setBike(bike);

        when(debtService.findOne(debtId)).thenReturn(mockDebt);
        when(clientService.findOne(1)).thenReturn(mockClient);
        when(bikesService.findById(1)).thenReturn(bike);
        String viewName = debtController.pay(paymentAmount, mockClient.getId(), bike.getId(), model);

        assertThat(viewName).isEqualTo("clients/show");
        verify(model).addAttribute(eq("client"),                eq(mockClient));
        verify(model).addAttribute(eq("bikes"),                 any(List.class));
        verify(model).addAttribute(eq("debts"),                 eq(Collections.emptyMap()));
        verify(model, times(1)).addAttribute(eq("errorMessage"), eq("The payment amount cannot be greater than the debt amount."));
    }


}
