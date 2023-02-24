# HEB Example Project - Shopping Cart Features

This project was completed as an example for a job interview with HEB

# Project Details
The project was implemented as a Maven project and built in IntelliJ using Java 17.  Maven contains all other dependencies.

It can be checked out directly in IntelliJ and run by launching com.mccants.heb.App as an "Application":

Here are the detail on the run configuration:
![image](https://user-images.githubusercontent.com/122554881/221070965-a3cd3678-52e4-4ef8-86c0-ab9d9c0c7b9f.png)

It can be build manually with:

mvn clean install

# End Points and Response

The four features requested can be accessed with the following end points:

* Feature 1 - `/receipt/total`
* Feature 2 - `/receipt/tax`
* Feature 3 - `/receipt/taxWithSubtotal`
* Feature 4 - `/receipt/totalWithCoupons`

All will return a JSON object like:
```
{
	"subTotal": 221.41,
	"discountTotal": 3.63,
	"discountedSubTotal": 217.78,
	"taxableSubTotal": 130.5,
	"taxTotal": 10.77,
	"grandTotal": 228.55
}
```
However, fields not required for the feature will be omitted. For example, a call to Feature 1 will return something like:
```
{
	"grandTotal": 228.55
}
```

# Design 

There is a controller for all our end points, which is com.mccants.heb.checkout.ReceiptController. 

All four end points, expect a JSON Cart as descibed in the problem explanation given.  It should always include some items and may optionally include coupons.

The controller relies on these Services:

* `com.mccants.heb.checkout.service.CartService` which manipulates the cart providing useful information, including discounts from any coupons found in the cart.
* `com.mccants.heb.checkout.service.SaleTaxService` which calculates SalesTax for some amounts.  

In any sort of exercise like this, there are naturally constraints that may not make sense.  
For instance, the cart is not stored somewhere in the backend (database) and then referenced, but passed into the controlle rin its entirity.
