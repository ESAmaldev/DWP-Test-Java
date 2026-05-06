package uk.gov.dwp.uc.pairtest;

import uk.gov.dwp.uc.pairtest.domain.TicketTypeRequest;
import uk.gov.dwp.uc.pairtest.exception.InvalidPurchaseException;

import thirdparty.paymentgateway.TicketPaymentService;
import thirdparty.seatbooking.SeatReservationService;

public class TicketServiceImpl implements TicketService {
    /**
     * Should only have private methods other than the one below.
     */

    private final TicketPaymentService ticketPaymentService;
    private final SeatReservationService seatReservationService;

    public TicketServiceImpl(TicketPaymentService ticketPaymentService, SeatReservationService seatReservationService) {
        this.ticketPaymentService = ticketPaymentService;
        this.seatReservationService = seatReservationService;
    }

    @Override
    public void purchaseTickets(Long accountId, TicketTypeRequest... ticketTypeRequests) throws InvalidPurchaseException {

        //Account ID validation
        if(accountId == null || accountId <=0){
            throw new InvalidPurchaseException("Invalid account ID" + accountId);
        }

        //Ticket count validation
        if(ticketTypeRequests == null || ticketTypeRequests.length == 0){
            throw new InvalidPurchaseException("Please select at least one ticket to continue the purchase");
        }

        //Ticket count by type
        int adults=0, childrens=0, infants=0;

        for(TicketTypeRequest req : ticketTypeRequests){

            if(req == null){
                throw new InvalidPurchaseException("No of tickets should not be null ");
            }

            TicketTypeRequest.Type type = req.getTicketType();

            if(type == TicketTypeRequest.Type.ADULT){
                adults += req.getNoOfTickets();
            } else if (type == TicketTypeRequest.Type.CHILD) {
                childrens += req.getNoOfTickets();
            }  else if (type == TicketTypeRequest.Type.INFANT) {
                infants += req.getNoOfTickets();
            }

        }

        //Check for maximum tickets purchased
        int totalTickets = adults + childrens + infants;
        if(totalTickets < 1){

            throw new InvalidPurchaseException("Please purchase at least one ticket");

        }
        if(totalTickets > 25){
            throw new InvalidPurchaseException("A maximum of 25 tickets can be purchased at once, please purchase the additional tickets separately" + totalTickets);
        }

        //Check that tickets for child/infant tickets are purchased with Adults to accompany them
        if((childrens > 0 || infants > 0 ) && adults ==0){
            throw new InvalidPurchaseException("Children and infant tickets cannot be purchased without adult ticket");
        }

        //Calculate Amount to pay

        int totalAmount = adults * 25 + childrens * 15;

        int seatsToReserve = adults + childrens;

        //Calling Payment Service to make payment
        ticketPaymentService.makePayment(accountId,totalAmount);

        /* Calling reservation service to allocate the seats.
           In real world scenario, the seatReservationService should only be called after getting a confirmation from payment service*/
        seatReservationService.reserveSeat(accountId,seatsToReserve);
    }

}