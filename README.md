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
However, fields not required for the feature will be omitted. For example, a call to Feature 1 will return:
```
{
	"grandTotal": 228.55
}
```

# Design 

## Controller 

The class com.mccants.heb.checkout.ReceiptController is the controller for all four end points.

All four end points, expect a JSON Cart as descibed in the problem explanation given.  It should always include some items and may optionally include coupons.  However, I did provide some tolerance for error, such as an empty list of items.

Under different circumstances, I'd consider combining all the End Points into one receipt End Point that takes some arguments either in the body or as URL parameters.  Also, I expect in a more realistic situation the cart would be referenced by some cart ID rather than passed in for every calculation.

A more realistic end point might look something like:

PUT /cart/<cart ID>/total?tax=true&showTaxSubtotal=true&applyCoupons=false

But that was well beyond the scope of this exercise.

## Services

The controller relies on these Services:

* `com.mccants.heb.checkout.service.CartService` which manipulates the cart providing useful information, including discounts from any coupons found in the cart.
* `com.mccants.heb.checkout.service.SaleTaxService` which calculates SalesTax for some amounts.  This was put in a separate service because sales tax calculations in reality are very complex.  While this exercise instructs me to greatly simplify the calculation, in a real world situation this would need to be at least a separate service and would likely involve additional data and calculations.

## DTOs

Data Transfer Objects (DTOs) are something that if not well managed in Spring can grow rapidly out of control.  For example, you could have four different Receipt DTOs, one for each feature.  I choose to simply this by having one class and instructing Jackson to only include non-null values.  This allows the appearance of each DTO returning just the fields requested, without actually having different DTO objects.

Likewise, I have only one Cart class for incoming JSON which can handle a cart with our without coupons in it.

## Money

I also select a Java Money implementation to represent the money values in the code.  This was done instead of using double or float which provide their own challenges with rounding.  The Sales Tax Service can take care of the rounding problems easliy using Monetary.getDefaultRounding() a Money object.  Another option would be to store all prices and discounts as a long or int that represents the number of cents.

One downside of the approach I took, is it does require special JSON Serializer and Deserializer code.  See com.mccants.heb.util.*Converter.

## Unit Testing

I wrote unit tests for the ReceiptController and both services.  This is a good way to make sure things are working correctly and makes refactoring easier as the test should help find any problems introduced.
