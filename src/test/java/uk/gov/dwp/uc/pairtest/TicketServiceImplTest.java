package uk.gov.dwp.uc.pairtest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;
import uk.gov.dwp.uc.pairtest.TicketServiceImpl;
import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketServiceImplTest {
    private thirdparty.paymentgateway.TicketPaymentService paymentService;
    private SeatReservationService reservationService;
    private TicketServiceImpl ticketService;

    @BeforeEach
    void setUp() {
        paymentService = mock(TicketPaymentService.class);
        reservationService = mock(SeatReservationService.class);
        ticketService = new TicketServiceImpl(paymentService, reservationService);
    }

    // ACCOUNT VALIDATION TESTS
    @Nested
    @DisplayName("Account Validation")
    class AccountValidation {

        @Test
        @DisplayName("Should throw exception when account ID is null")
        void shouldThrowExceptionWhenAccountIdIsNull() {
            InvalidPurchaseException exception = assertThrows(
                    InvalidPurchaseException.class,
                    () -> ticketService.purchaseTickets(
                            null,
                            new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1)
                    )
            );
            assertTrue(exception.getMessage().toLowerCase().contains("account"));
        }

        @Test
        @DisplayName("Should throw exception when account ID is zero")
        void shouldThrowExceptionWhenAccountIdIsZero() {
            InvalidPurchaseException exception = assertThrows(
                    InvalidPurchaseException.class,
                    () -> ticketService.purchaseTickets(
                            0L,
                            new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1)
                    )
            );
            assertTrue(exception.getMessage().toLowerCase().contains("account"));
        }

        @Test
        @DisplayName("Should throw exception when account ID is negative")
        void shouldThrowExceptionWhenAccountIdIsNegative() {
            InvalidPurchaseException exception = assertThrows(
                    InvalidPurchaseException.class,
                    () -> ticketService.purchaseTickets(
                            -1L,
                            new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1)
                    )
            );
            assertTrue(exception.getMessage().toLowerCase().contains("account"));
        }
    }

    // REQUEST VALIDATION TESTS
    @Nested
    @DisplayName("Request Validation")
    class RequestValidation {

        @Test
        @DisplayName("Should throw exception when requests array is null")
        void shouldThrowExceptionWhenRequestsArrayIsNull() {
            InvalidPurchaseException exception = assertThrows(
                    InvalidPurchaseException.class,
                    () -> ticketService.purchaseTickets(1L, (TicketTypeRequest[]) null)
            );
            assertTrue(exception.getMessage().toLowerCase().contains("at least one ticket"));
        }

        @Test
        @DisplayName("Should throw exception when no requests provided")
        void shouldThrowExceptionWhenNoRequestsProvided() {
            InvalidPurchaseException exception = assertThrows(
                    InvalidPurchaseException.class,
                    () -> ticketService.purchaseTickets(1L)
            );
            assertTrue(exception.getMessage().toLowerCase().contains("at least one ticket"));
        }

        @Test
        @DisplayName("Should throw exception when a request is null")
        void shouldThrowExceptionWhenRequestIsNull() {
            InvalidPurchaseException exception = assertThrows(
                    InvalidPurchaseException.class,
                    () -> ticketService.purchaseTickets(
                            1L,
                            new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                            null
                    )
            );
            assertTrue(exception.getMessage().toLowerCase().contains("null"));
        }
    }

    // MAX TICKETS VALIDATION TESTS
    @Nested
    @DisplayName("Maximum Tickets Validation")
    class MaxTicketsValidation {

        @Test
        @DisplayName("Should throw exception when total tickets exceed 25")
        void shouldThrowExceptionWhenTotalTicketsExceed25() {
            InvalidPurchaseException exception = assertThrows(
                    InvalidPurchaseException.class,
                    () -> ticketService.purchaseTickets(
                            1L,
                            new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 20),
                            new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 6)
                    )
            );
            assertTrue(exception.getMessage().toLowerCase().contains("25"));
        }

        @Test
        @DisplayName("Should allow exactly 25 tickets")
        void shouldAllowExactly25Tickets() {
            assertDoesNotThrow(() ->
                    ticketService.purchaseTickets(
                            1L,
                            new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 25)
                    )
            );
        }

        @Test
        @DisplayName("Should throw exception when total tickets is zero")
        void shouldThrowExceptionWhenTotalTicketsIsZero() {
            InvalidPurchaseException exception = assertThrows(
                    InvalidPurchaseException.class,
                    () -> ticketService.purchaseTickets(
                            1L,
                            new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 0)
                    )
            );
            assertTrue(exception.getMessage().toLowerCase().contains("at least one"));
        }
    }

    // ADULT REQUIRED VALIDATION TESTS
    @Nested
    @DisplayName("Adult Required Validation")
    class AdultRequiredValidation {

        @Test
        @DisplayName("Should throw exception when child tickets purchased without adult")
        void shouldThrowExceptionWhenChildTicketsWithoutAdult() {
            InvalidPurchaseException exception = assertThrows(
                    InvalidPurchaseException.class,
                    () -> ticketService.purchaseTickets(
                            1L,
                            new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 2)
                    )
            );
            assertTrue(exception.getMessage().toLowerCase().contains("adult"));
        }

        @Test
        @DisplayName("Should throw exception when infant tickets purchased without adult")
        void shouldThrowExceptionWhenInfantTicketsWithoutAdult() {
            InvalidPurchaseException exception = assertThrows(
                    InvalidPurchaseException.class,
                    () -> ticketService.purchaseTickets(
                            1L,
                            new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2)
                    )
            );
            assertTrue(exception.getMessage().toLowerCase().contains("adult"));
        }

        @Test
        @DisplayName("Should throw exception when child and infant tickets purchased without adult")
        void shouldThrowExceptionWhenChildAndInfantTicketsWithoutAdult() {
            InvalidPurchaseException exception = assertThrows(
                    InvalidPurchaseException.class,
                    () -> ticketService.purchaseTickets(
                            1L,
                            new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 1),
                            new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)
                    )
            );
            assertTrue(exception.getMessage().toLowerCase().contains("adult"));
        }

        @Test
        @DisplayName("Should allow adult only tickets")
        void shouldAllowAdultOnlyTickets() {
            assertDoesNotThrow(() ->
                    ticketService.purchaseTickets(
                            1L,
                            new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 3)
                    )
            );
        }
    }

    // PAYMENT CALCULATION TESTS
    @Nested
    @DisplayName("Payment Calculation")
    class PaymentCalculation {

        @Test
        @DisplayName("Should charge correct amount for adults only")
        void shouldChargeCorrectAmountForAdultsOnly() {
            ticketService.purchaseTickets(
                    1L,
                    new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 3)
            );
            verify(paymentService).makePayment(1L, 75); // 3 × £25
        }

        @Test
        @DisplayName("Should charge correct amount for children only with adult")
        void shouldChargeCorrectAmountForChildrenWithAdult() {
            ticketService.purchaseTickets(
                    1L,
                    new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                    new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 3)
            );
            verify(paymentService).makePayment(1L, 70); // £25 + (3 × £15)
        }

        @Test
        @DisplayName("Should not charge for infants")
        void shouldNotChargeForInfants() {
            ticketService.purchaseTickets(
                    1L,
                    new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
                    new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 3)
            );
            verify(paymentService).makePayment(1L, 50); // 2 × £25, infants free
        }

        @Test
        @DisplayName("Should charge correct amount for mixed ticket types")
        void shouldChargeCorrectAmountForMixedTicketTypes() {
            ticketService.purchaseTickets(
                    1L,
                    new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
                    new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 3),
                    new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 1)
            );
            verify(paymentService).makePayment(1L, 95); // (2×25) + (3×15) + 0
        }
    }

    // SEAT RESERVATION TESTS
    @Nested
    @DisplayName("Seat Reservation")
    class SeatReservation {

        @Test
        @DisplayName("Should reserve seats only for adults and children")
        void shouldReserveSeatsOnlyForAdultsAndChildren() {
            ticketService.purchaseTickets(
                    1L,
                    new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 2),
                    new TicketTypeRequest(TicketTypeRequest.Type.CHILD, 3),
                    new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 2)
            );
            verify(reservationService).reserveSeat(1L, 5); // 2 adults + 3 children
        }

        @Test
        @DisplayName("Should not reserve seats for infants")
        void shouldNotReserveSeatsForInfants() {
            ticketService.purchaseTickets(
                    1L,
                    new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 1),
                    new TicketTypeRequest(TicketTypeRequest.Type.INFANT, 4)
            );
            verify(reservationService).reserveSeat(1L, 1); // 1 adult only
        }

        @Test
        @DisplayName("Should reserve correct number of seats for adults only")
        void shouldReserveCorrectSeatsForAdultsOnly() {
            ticketService.purchaseTickets(
                    1L,
                    new TicketTypeRequest(TicketTypeRequest.Type.ADULT, 5)
            );
            verify(reservationService).reserveSeat(1L, 5);
        }
    }



}