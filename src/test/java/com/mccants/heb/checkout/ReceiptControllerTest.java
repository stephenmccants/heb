package com.mccants.heb.checkout;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReceiptControllerTest {
    /** The port the tests will run on */
    @Value("${local.server.port}")
    int port;

    /** Useful to pass in an empty cart and make sure we handle it correctly */
    final static String cartEmpty = "{\n \"items\": []\n}";
    /** Test one single item - very basic */
    final static String cartOneItem =
            """
                    {
                      "items": [
                        {
                          "itemName": "H-E-B Two Bite Brownies",
                          "sku": 85294241,
                          "isTaxable": false,
                          "ownBrand": true,
                          "price": 3.60
                        }
                      ]
                    }""";
    /** Test one single item - very basic and taxable */
    final static String cartOneTaxableItem =
            """
                    {
                      "items": [
                        {
                          "itemName": "H-E-B Two Bite Brownies",
                          "sku": 85294241,
                          "isTaxable": true,
                          "ownBrand": true,
                          "price": 3.60
                        }
                      ]
                    }""";
    /** Test three items - typically enough to catch problems handling multiple items */
    final static String cartThreeItems =
            """
                    {
                      "items": [
                        {
                          "itemName": "H-E-B Two Bite Brownies",
                          "sku": 85294241,
                          "isTaxable": true,
                          "ownBrand": true,
                          "price": 3.61
                        },
                        {
                          "itemName": "Halo Top Vanilla Bean Ice Cream",
                          "sku": 95422042,
                          "isTaxable": false,
                          "ownBrand": false,
                          "price": 3.31
                        },
                        {
                          "itemName": "H-E-B Select Ingredients Creamy Creations Vanilla Bean Ice Cream",
                          "sku": 64267055,
                          "isTaxable": true,
                          "ownBrand": true,
                          "price": 9.83
                        }
                      ]
                    }""";
    final static String cartThreeItemsTwoCoupons =
            """
                    {
                      "items": [
                        {
                          "itemName": "H-E-B Two Bite Brownies",
                          "sku": 85294241,
                          "isTaxable": true,
                          "ownBrand": true,
                          "price": 3.61
                        },
                        {
                          "itemName": "Halo Top Vanilla Bean Ice Cream",
                          "sku": 95422042,
                          "isTaxable": false,
                          "ownBrand": false,
                          "price": 3.31
                        },
                        {
                          "itemName": "H-E-B Select Ingredients Creamy Creations Vanilla Bean Ice Cream",
                          "sku": 64267055,
                          "isTaxable": true,
                          "ownBrand": true,
                          "price": 9.83
                        }
                      ],
                      "coupons": [
                        {
                          "couponName": "Coupon One",
                          "appliedSku": 85294241,
                          "discountPrice": 0.79
                        },
                        {
                          "couponName": "Coupon Two - does not apply",
                          "appliedSku": 30532705,
                          "discountPrice": 1.83
                        },
                        {
                          "couponName": "Coupon Three - ice cream!",
                          "appliedSku": 95422042,
                          "discountPrice": 5.00
                        }
                      ]
                    }
                    
                    """;

    /**
     * Sets up the port for our test to run on
     */
    @BeforeEach
    public void setUp() {
        RestAssured.port = port;
    }

    /**
     * Test case showing Feature 1 does not explode if passed an empty body
     */
    @Test
    public void testTotalOnlyReceipt_Blank() {
        // Make the post call
        ExtractableResponse<Response> extract = given().body("").contentType(ContentType.JSON).post("/receipt/total")
                .then().extract();
        // Verify that it is a 400 return code
        assertThat(extract.statusCode(), is(400)); // Bad Request
        assertThat(extract.body().jsonPath().get("error"), is("Bad Request"));
    }

    /**
     * Test case showing Feature 1 does not explode if passed no items
     */
    @Test
    public void testTotalOnlyReceipt_NoItems() {
        // Make the post call
        ExtractableResponse<Response> extract = given().body(cartEmpty).contentType(ContentType.JSON).post("/receipt/total")
                .then().extract();
        // Verify that it is a 200 return code
        assertThat(extract.statusCode(), is(200));
        assertThat(extract.body().jsonPath().get("subTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("discountTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("discountedSubTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("taxableSubTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("taxTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("grandTotal"), is(0));
    }

    /**
     * Test case showing Feature 1 works correctly it passed one item
     */
    @Test
    public void testTotalOnlyReceipt_OneItem() {
        // Make the post call
        ExtractableResponse<Response> extract = given().body(cartOneItem).contentType(ContentType.JSON).post("/receipt/total")
                .then().extract();
        // Verify that it is a 200 return code
        assertThat(extract.statusCode(), is(200));
        assertThat(extract.body().jsonPath().get("subTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("discountTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("discountedSubTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("taxableSubTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("taxTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("grandTotal"), is(3.6f));
    }

    /**
     * Test case showing Feature 1 works correctly it passed three item
     */
    @Test
    public void testTotalOnlyReceipt_ThreeItems() {
        // Make the post call
        ExtractableResponse<Response> extract = given().body(cartThreeItems).contentType(ContentType.JSON).post("/receipt/total")
                .then().extract();
        // Verify that it is a 200 return code
        assertThat(extract.statusCode(), is(200));
        assertThat(extract.body().jsonPath().get("subTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("discountTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("discountedSubTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("taxableSubTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("taxTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("grandTotal"), is(16.75f));
    }

    /**
     * Test case showing Feature 2 does not explode if passed an empty body
     */
    @Test
    public void testTaxOnlyReceipt_Blank() {
        // Make the post call
        ExtractableResponse<Response> extract = given().body("").contentType(ContentType.JSON).post("/receipt/tax")
                .then().extract();
        // Verify that it is a 400 return code
        assertThat(extract.statusCode(), is(400)); // Bad Request
        assertThat(extract.body().jsonPath().get("error"), is("Bad Request"));
    }

    /**
     * Test case showing Feature 2 does not explode if passed no items
     */
    @Test
    public void testTaxOnlyReceipt_NoItems() {
        // Make the post call
        ExtractableResponse<Response> extract = given().body(cartEmpty).contentType(ContentType.JSON).post("/receipt/tax")
                .then().extract();
        // Verify that it is a 200 return code
        assertThat(extract.statusCode(), is(200));
        assertThat(extract.body().jsonPath().get("subTotal"), is(0));
        assertThat(extract.body().jsonPath().get("discountTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("discountedSubTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("taxableSubTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("taxTotal"), is(0));
        assertThat(extract.body().jsonPath().get("grandTotal"), is(0));
    }

    /**
     * Test case showing Feature 3 does not explode if passed no items
     */
    @Test
    public void testTaxOnlyReceiptWithSubTotal_NoItems() {
        // Now explicitly with tax subTotal
        ExtractableResponse<Response> extract = given().body(cartEmpty).contentType(ContentType.JSON).post("/receipt/taxWithSubtotal")
                .then().extract();
        // Verify that it is a 200 return code
        assertThat(extract.statusCode(), is(200));
        assertThat(extract.body().jsonPath().get("subTotal"), is(0));
        assertThat(extract.body().jsonPath().get("discountTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("discountedSubTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("taxableSubTotal"), is(0));
        assertThat(extract.body().jsonPath().get("taxTotal"), is(0));
        assertThat(extract.body().jsonPath().get("grandTotal"), is(0));
    }

    /**
     * Test case showing Feature 2 works correctly it passed one item
     */
    @Test
    public void testTaxOnlyReceipt_OneItem_NoTax() {
        // Make the post call
        ExtractableResponse<Response> extract = given().body(cartOneItem).contentType(ContentType.JSON).post("/receipt/tax")
                .then().extract();
        // Verify that it is a 200 return code
        assertThat(extract.statusCode(), is(200));
        assertThat(extract.body().jsonPath().get("subTotal"), is(3.6f));
        assertThat(extract.body().jsonPath().get("discountTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("discountedSubTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("taxableSubTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("taxTotal"), is(0));
        assertThat(extract.body().jsonPath().get("grandTotal"), is(3.6f));
    }

    /**
     * Test case showing Feature 3 works correctly it passed one item
     */
    @Test
    public void testTaxOnlyReceiptWithSubTotal_OneItem_NoTax() {
        // Now explicitly with tax subTotal
        ExtractableResponse<Response> extract = given().body(cartOneItem).contentType(ContentType.JSON).post("/receipt/taxWithSubtotal")
                .then().extract();
        // Verify that it is a 200 return code
        assertThat(extract.statusCode(), is(200));
        assertThat(extract.body().jsonPath().get("subTotal"), is(3.6f));
        assertThat(extract.body().jsonPath().get("discountTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("discountedSubTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("taxableSubTotal"), is(0));
        assertThat(extract.body().jsonPath().get("taxTotal"), is(0));
        assertThat(extract.body().jsonPath().get("grandTotal"), is(3.6f));
    }

    /**
     * Test case showing Feature 2 works correctly it passed one item
     */
    @Test
    public void testTaxOnlyReceipt_OneItem() {
        // Make the post call
        ExtractableResponse<Response> extract = given().body(cartOneTaxableItem).contentType(ContentType.JSON).post("/receipt/tax")
                .then().extract();
        // Verify that it is a 200 return code
        assertThat(extract.statusCode(), is(200));
        assertThat(extract.body().jsonPath().get("subTotal"), is(3.6f));
        assertThat(extract.body().jsonPath().get("discountTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("discountedSubTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("taxableSubTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("taxTotal"), is(0.3f));
        assertThat(extract.body().jsonPath().get("grandTotal"), is(3.9f));
    }

    /**
     * Test case showing Feature 3 works correctly it passed one item
     */
    @Test
    public void testTaxOnlyReceiptWithSubTotal_OneItem() {
        // Now explicitly with tax subTotal
        ExtractableResponse<Response> extract = given().body(cartOneTaxableItem).contentType(ContentType.JSON).post("/receipt/taxWithSubtotal")
                .then().extract();
        // Verify that it is a 200 return code
        assertThat(extract.statusCode(), is(200));
        assertThat(extract.body().jsonPath().get("subTotal"), is(3.6f));
        assertThat(extract.body().jsonPath().get("discountTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("discountedSubTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("taxableSubTotal"), is(3.6f));
        assertThat(extract.body().jsonPath().get("taxTotal"), is(0.3f));
        assertThat(extract.body().jsonPath().get("grandTotal"), is(3.9f));
    }

    /**
     * Test case showing Feature 2 works correctly it passed three item
     */
    @Test
    public void testTaxOnlyReceipt_ThreeItems() {
        // Make the post call - no tax subtotal
        ExtractableResponse<Response> extract = given().body(cartThreeItems).contentType(ContentType.JSON).post("/receipt/tax")
                .then().extract();
        // Verify that it is a 200 return code
        assertThat(extract.statusCode(), is(200));
        assertThat(extract.body().jsonPath().get("subTotal"), is(16.75f));
        assertThat(extract.body().jsonPath().get("discountTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("discountedSubTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("taxableSubTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("taxTotal"), is(1.11f));
        assertThat(extract.body().jsonPath().get("grandTotal"), is(17.86f));
    }

    /**
     * Test case showing Feature 3 works correctly it passed three item
     */
    @Test
    public void testTaxOnlyReceiptWithSubTotal_ThreeItems() {
        // Now explicitly with tax subTotal
        ExtractableResponse<Response> extract = given().body(cartThreeItems).contentType(ContentType.JSON).post("/receipt/taxWithSubtotal")
                .then().extract();
        // Verify that it is a 200 return code
        assertThat(extract.statusCode(), is(200));
        assertThat(extract.body().jsonPath().get("subTotal"), is(16.75f));
        assertThat(extract.body().jsonPath().get("discountTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("discountedSubTotal"), is(nullValue()));
        assertThat(extract.body().jsonPath().get("taxableSubTotal"), is(13.44f));
        assertThat(extract.body().jsonPath().get("taxTotal"), is(1.11f));
        assertThat(extract.body().jsonPath().get("grandTotal"), is(17.86f));
    }

    @Test
    public void testTotalWithCoupons() {
         ExtractableResponse<Response> extract = given().body(cartThreeItemsTwoCoupons).contentType(ContentType.JSON).post("/receipt/totalWithCoupons")
                .then().extract();
         // Verify that it is a 200 return code
        assertThat(extract.statusCode(), is(200));
        assertThat(extract.body().jsonPath().get("subTotal"), is(16.75f));
        assertThat(extract.body().jsonPath().get("discountTotal"), is(4.10f));
        assertThat(extract.body().jsonPath().get("discountedSubTotal"), is(12.65f));
        assertThat(extract.body().jsonPath().get("taxableSubTotal"), is(12.65f));
        assertThat(extract.body().jsonPath().get("taxTotal"), is(1.04f));
        assertThat(extract.body().jsonPath().get("grandTotal"), is(13.69f));
    }
}
