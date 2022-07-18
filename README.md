# myCoinCap
Crypto Asset information project based off of CoinCap API https://docs.coincap.io/


## Run App
With project cloned locally, Run containers with Docker to connect to MongoDB. As seen in docker-compose.yaml, container 'mongodb2' has port 27017, and 'mongo-express2' has port 8081. Can view Data via Mongo Express: http://localhost:8081/db/myCoinCap/wallet.

Use gradle to run application. (Port 8080)

I've provided a POSTMAN collection to view available endpoints: https://www.getpostman.com/collections/3cc970fc58aaa9c88ed9


# Documentation

## Get full list of Assets
**URI: myCoinCap/v1/assets**


can pass in filters and pagination


Example 1: **/myCoinCap/v1/assets?ids=bitcoin,ethereum**

Will return only info for bitcoin and ethereum

</br>

Example 2: **/myCoinCap/v1/assets?limit=3**

Will only return the top 3 assets

## Get Single Asset based on ID
**URI: /myCoinCap/v1/assets/#id#**

Example: **/myCoinCap/v1/assets/bitcoin**
Will return info for bitcoin


## Create a Wallet (POST)
**URI: /myCoinCap/v1/wallet/create**

Sample Request:
```
{
    "id": "0007",
    "assets": {
        "bitcoin": 1,
        "ethereum": 5.5
    }
}
```
Takes in an ID (must be new or will return message saying already exists) and a list of assets with corresponding numbers for how much of that asset you own.
</br>
200: Responds with String saying wallet with id was created.


## Edit your wallet (PUT)
**URI: /myCoinCap/v1/wallet/edit**

Sample Request:
```
{
    "id": "0007",
    "assets": {
        "bitcoin": 10,
        "ethereum": 5.5
    }
}
```
Same as Create (above), Takes in ID (must already exist, if it doesn't will prompt you to create a wallet first) and a list of assets with corresponding numbers for how much of that asset you own. Will update your exisiting list of assets with the new values.
</br></br>

200: Responds with body including your Id and list of updated assets

## Get Currend Wallet Value
**URI: /myCoinCap/v1/wallet/value/#id#**

</br>
Returns your total current value in USD along with list of current values for each asset you own (Current value of asset multiplied by how many you own)

## Get Historic Wallet Value
**URI: /myCoinCap/v1/wallet/history/#id#/#timeUnit#/#timeAmount#**

TimeUnit can be: day, minute, hour
TimeAmount is any integer that fits within the last year.

</br>
Example: **/myCoinCap/v1/wallet/history/0007/day/2** will return your wallet value history from the past 2 days

</br>
200: Responds with total net Gain/Loss over specified period of time, the specified period of time, and List of Net gain/loss per Asset

Sample Response:
```
{
    "id": "0007",
    "totalNetGainLoss": 9401.251410628553,
    "duration": "In the past 2 days.",
    "perAssetHistory": [
        {
            "assetName": "ethereum",
            "netGainLoss": 1273.72250167848705360,
            "sinceTime": 1657929600000
        },
        {
            "assetName": "bitcoin",
            "netGainLoss": 8127.5289089500468358,
            "sinceTime": 1657929600000
        }
    ]
}
```
