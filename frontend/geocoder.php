<?php
require("_inc/OAuth.php");

$url = "http://yboss.yahooapis.com/geo/placefinder";
$cc_key= "";
$cc_secret = "";

if($_REQUEST['q'] == "" || strtoupper($_REQUEST['q']) == "CURRENT LOCATION") {
	return;
}

$args = array();
$args["q"] = $_REQUEST['q'] . " New York City";
$args["flags"] = "J";

$consumer = new OAuthConsumer($cc_key, $cc_secret);
$request = OAuthRequest::from_consumer_and_token($consumer, NULL,"GET", $url, $args);
$request->sign_request(new OAuthSignatureMethod_HMAC_SHA1(), $consumer, NULL);

$url = sprintf("%s?%s", $url, OAuthUtil::build_http_query($args));

$ch = curl_init();
$headers = array($request->to_header());
curl_setopt($ch,CURLOPT_ENCODING , "gzip"); 
curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
curl_setopt($ch, CURLOPT_URL, $url);
curl_setopt($ch, CURLOPT_RETURNTRANSFER, TRUE);

$rsp = curl_exec($ch);
$data = json_decode($rsp);

if($data->bossresponse->responsecode != 200)
	return;

if(intval($data->bossresponse->placefinder->results[0]->radius) > 1000)
	return;

$r = array();
$r['latitude'] = $data->bossresponse->placefinder->results[0]->latitude;
$r['longitude'] = $data->bossresponse->placefinder->results[0]->longitude;
echo json_encode($r);

