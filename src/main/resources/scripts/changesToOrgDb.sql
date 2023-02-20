
use prajek5_stage


----CUSTOMER
 -- update VAT 
 
    update tax_config set LABEL = 'VAT (12%)' , value =12;
    update configuration SET  CONFIG_VALUE='12' WHERE CONFIG_NAME ='VAT_PERC';
   
 

-- Book apt add amt and time

 update static_content set CONTENT = 'By booking this appointment, I agree to the following:<br/><br/>1. The Pro that I selected is an independent contractor and not an employee of Navalta-Chavan Technologies Corp. or Prajekpro <br/><br/> 2. I voluntarily selected the Pro which I booked.<br/><br/>3. I booked the Pro for a lawful purpose only.<br/><br/>4. I am entitled to cancel the booking within {0} minutes after the Pro has been booked. Otherwise, I am liable to pay a cancellation fee in the amount of {1}.' where CONTENT_ID='CUST_BOOK_APPT'
  
  
  
 -- Booking terms and conditions :

 INSERT INTO static_content
(CONTENT_ID, CONTENT, ACTIVE_STATUS, CREATED_BY, CREATED_TS, MODIFIED_BY, MODIFIED_TS)
VALUES('BOOK_APPT_BY_CUST', 'Terms & Conditions: By booking this appointment, I acknowledge, understand, and agree that the Pro I selected is not employed by PrajekPro but an independent contractor. I agree that I made my research before selecting a Pro and that I selected the Pro according to my own will. I agree that I booked this service/project appointment with no intention of doing any fraudulent and/or illegal, breaking this may cost me a penalty fee and may be subject to legal action. I understand that I have to cancel within the cancellation period set by the PRO, failure to comply- I agreed that I shall have to pay a cancellation fee.',
 1, 'akshataj', now(), 'akshataj', now());
 
 
 
 -- PRO :

 -- Apply Coupon 
  update coupon_code_details set ACTIVE_STATUS = 0 where COUPON_CODE = 'Cooperatives2022';

	INSERT INTO coupon_code_details
	(COUPON_CODE, VALIDITY_PER_USER, COUPON_CODE_TYPE, META_DATA, CREATED_BY, MODIFIED_BY, CREATED_TS, MODIFIED_TS, ACTIVE_STATUS)
	VALUES('Prajekpro2023', 1, 1, '3', 'akshataj', 'akshataj', now(), now(), 1);

 --- Accepting Proj disclaimer :
	INSERT INTO static_content
	(CONTENT_ID, CONTENT, ACTIVE_STATUS, CREATED_BY, CREATED_TS, MODIFIED_BY, MODIFIED_TS)
	VALUES('PRO_ACCEPT_PROJ', 'By accepting this project, I agree to the following:<br/><br/>1. I guarantee the quality of my work and its delivery on the time agreed upon.;<br/><br/>2. I will not sue my customer''s information for any purpose it was given;<br/><br/>3. In case of breach of this Agreement on my part, my profile in Prajekpro will be permanently banned and I will pay a penaily fee in the amount up to One hundred Thousand Pesos.;<br/><br/> 4.  4. All damages which may be caused by my work shall be my sole liability and; <br/>5. Any refund that a customer may be entitled to by reason of my work  shall be shouldered by me either via cash-on-hand or may be deducted from my PRO wallet.',
	 1, 'akshataj', now(), 'akshataj', now());

 
