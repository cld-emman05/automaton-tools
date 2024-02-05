@Grab(group='org.codehaus.groovy.modules.http-builder', module='http-builder', version='0.7' )

import groovyx.net.http.HTTPBuilder
import static groovyx.net.http.ContentType.URLENC
 
def http = new HTTPBuilder('https://testpay.cliqq.net')
def postBody = [merchantID:'ATI', merchantRef:'6419705015',
    amount:'10', token:'235a23122139152ff830aa7fa1a876a95d4e365b',
    email:'atidavao@apollo.com.ph', receiptRemarks:'ATI |^MerchRef: 20170307',
    returnPaymentDetails:'Y', transactionDescription:'PLDT Payment',
    payLoad:'payLoad.', productId:'11940058',
    serviceCharge:'10.00',commission:'3.00',
    type:'SALES']
def payId;

http.post(path:'/v1/reference', body: postBody, requestContentType:URLENC) { resp, reader -> 
   // println "POST Success: ${resp.statusLine}"
    assert resp.statusLine.statusCode == 201
    payId = reader['payID']
}