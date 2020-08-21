package com.redhotapp.driverapp.data.remote

import android.util.Log
import com.redhotapp.driverapp.BuildConfig
import com.redhotapp.driverapp.data.Constant
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.io.IOException

class MockInterceptor : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return if (BuildConfig.DEBUG) {
            Log.d(MockInterceptor::class.java.canonicalName, "response mocked")
            val uri = chain.request().url.toUri().toString()
            if (uri.contains("GetAllTask")) {
                val responce = testResponse
                return chain.proceed(chain.request())
                    .newBuilder()
                    .code(Constant.SUCCESS_CODE)
                    .protocol(Protocol.HTTP_2)
                    .message(responce)
                    .addHeader("content-type", "application/json")
                    .body(responce.toResponseBody("application/json".toMediaTypeOrNull()))
                    .build()
            }
            chain.proceed(chain.request())
        } else {
            throw IllegalAccessError(
                "MockInterceptor is only meant for Testing Purposes and " +
                        "bound to be used only with DEBUG mode"
            )
        }
    }

    //    /**
    private val testResponse: String =
        "{\"IsSuccess\":true,\"IsException\":false,\"Text\":null,\"ExceptionText\":null,\"CommunicationItem\":null,\"AllTask\":[{\"MandantId\":3,\"MandantName\":\"Hegelmann Express GmbH\",\"TaskId\":1213,\"TaskChangeId\":2331,\"AbonaTransferNr\":\"\",\"PreviousTaskId\":0,\"NextTaskId\":1215,\"VehiclePreviousTaskId\":0,\"VehicleNextTaskId\":0,\"ChangeReason\":1,\"ActionType\":0,\"OrderNo\":202023123,\"Description\":null,\"KundenName\":\"Amazon\",\"KundenNr\":30118,\"ReferenceIdCustomer1\":\"a\",\"ReferenceIdCustomer2\":null,\"PalletsAmount\":0,\"TaskDueDateStart\":\"0001-01-01T00:00:00\"," +
                "\"TaskDueDateFinish\":\"" +
                "2020-09-20T17:27:00" +
                "\",\"Status\":0,\"PercentFinishedActivities\":0,\"OrderDetails\":{\"OrderNo\":202023123,\"CustomerName\":\"Amazon\",\"CustomerNo\":30118,\"ReferenceIdCustomer1\":\"a\",\"ReferenceIdCustomer2\":null},\"TaskDetails\":{\"Description\":null,\"LoadingOrder\":0,\"ReferenceId1\":null,\"ReferenceId2\":null},\"Address\":{\"Name1\":null,\"Name2\":null,\"Street\":\"Vulytsia Balzaka\",\"ZIP\":null,\"City\":\"Kyiv\",\"State\":null,\"Nation\":\"UA\",\"Longitude\":30.59955,\"Latitude\":50.51487,\"Note\":null},\"SwapInfoItem\":null,\"PalletExchange\":{\"ExchangeType\":0,\"PalletsAmount\":0,\"IsDPL\":false},\"DangerousGoods\":{\"IsGoodsDangerous\":false,\"ADRClass\":null,\"DangerousGoodsClassType\":0,\"UNNo\":null},\"Activities\":[{\"MandantId\":3,\"TaskId\":1213,\"ActivityId\":12,\"Name\":\"Driving to loading\",\"Description\":null,\"Started\":\"2020-06-02T12:47:08\",\"Finished\":\"2020-06-02T12:47:09\",\"Status\":2,\"Sequence\":0,\"CustomActivityId\":0,\"DeviceId\":null,\"DelayReasons\":null},{\"MandantId\":3,\"TaskId\":1213,\"ActivityId\":13,\"Name\":\"Waiting for loading\",\"Description\":null,\"Started\":\"2020-06-02T12:47:09\",\"Finished\":\"2020-06-02T12:47:12\",\"Status\":2,\"Sequence\":1,\"CustomActivityId\":0,\"DeviceId\":null,\"DelayReasons\":null},{\"MandantId\":3,\"TaskId\":1213,\"ActivityId\":14,\"Name\":\"Loading\",\"Description\":null,\"Started\":\"2020-06-02T12:47:12\",\"Finished\":\"2020-06-02T12:47:14\",\"Status\":2,\"Sequence\":2,\"CustomActivityId\":0,\"DeviceId\":null,\"DelayReasons\":null}],\"Contacts\":[{\"ContactType\":0,\"NumberType\":0,\"Name\":null,\"Number\":null}],\"Notes\":null,\"ChangedItems\":[1]},{\"MandantId\":3,\"MandantName\":\"Hegelmann Express GmbH\",\"TaskId\":1218,\"TaskChangeId\":2343,\"AbonaTransferNr\":\"\",\"PreviousTaskId\":0,\"NextTaskId\":1220,\"VehiclePreviousTaskId\":0,\"VehicleNextTaskId\":0,\"ChangeReason\":0,\"ActionType\":0,\"OrderNo\":202023125,\"Description\":null,\"KundenName\":\"Amazon\",\"KundenNr\":30118,\"ReferenceIdCustomer1\":\"a\",\"ReferenceIdCustomer2\":null,\"PalletsAmount\":0,\"TaskDueDateStart\":\"0001-01-01T00:00:00\"," +
                "\"TaskDueDateFinish\":\"" +
                "2020-07-20T17:27:00" +
                "\",\"Status\":0,\"PercentFinishedActivities\":0,\"OrderDetails\":{\"OrderNo\":202023125,\"CustomerName\":\"Amazon\",\"CustomerNo\":30118,\"ReferenceIdCustomer1\":\"a\",\"ReferenceIdCustomer2\":null},\"TaskDetails\":{\"Description\":null,\"LoadingOrder\":0,\"ReferenceId1\":null,\"ReferenceId2\":null},\"Address\":{\"Name1\":null,\"Name2\":null,\"Street\":\"bal 1\",\"ZIP\":\"31660\",\"City\":\"bal\",\"State\":null,\"Nation\":\"FR\",\"Longitude\":0,\"Latitude\":0,\"Note\":null},\"SwapInfoItem\":null,\"PalletExchange\":{\"ExchangeType\":0,\"PalletsAmount\":0,\"IsDPL\":false},\"DangerousGoods\":{\"IsGoodsDangerous\":false,\"ADRClass\":null,\"DangerousGoodsClassType\":0,\"UNNo\":null},\"Activities\":[{\"MandantId\":3,\"TaskId\":1218,\"ActivityId\":12,\"Name\":\"Driving to loading\",\"Description\":null,\"Started\":\"2020-06-02T10:35:01.443\",\"Finished\":\"2020-06-02T10:36:01.443\",\"Status\":2,\"Sequence\":0,\"CustomActivityId\":0,\"DeviceId\":null,\"DelayReasons\":null},{\"MandantId\":3,\"TaskId\":1218,\"ActivityId\":13,\"Name\":\"Waiting for loading\",\"Description\":null,\"Started\":\"2020-06-02T10:36:01\",\"Finished\":\"2020-06-02T12:47:23\",\"Status\":2,\"Sequence\":1,\"CustomActivityId\":0,\"DeviceId\":null,\"DelayReasons\":null},{\"MandantId\":3,\"TaskId\":1218,\"ActivityId\":14,\"Name\":\"Loading\",\"Description\":null,\"Started\":\"2020-06-02T12:47:23\",\"Finished\":\"2020-06-02T12:47:25\",\"Status\":2,\"Sequence\":2,\"CustomActivityId\":0,\"DeviceId\":null,\"DelayReasons\":null}],\"Contacts\":[{\"ContactType\":0,\"NumberType\":0,\"Name\":null,\"Number\":null}],\"Notes\":null,\"ChangedItems\":null},{\"MandantId\":3,\"MandantName\":\"Hegelmann Express GmbH\",\"TaskId\":1228,\"TaskChangeId\":2385,\"AbonaTransferNr\":\"\",\"PreviousTaskId\":0,\"NextTaskId\":1229,\"VehiclePreviousTaskId\":0,\"VehicleNextTaskId\":0,\"ChangeReason\":3,\"ActionType\":0,\"OrderNo\":202023126,\"Description\":null,\"KundenName\":\"Amazon\",\"KundenNr\":30118,\"ReferenceIdCustomer1\":\"a\",\"ReferenceIdCustomer2\":null,\"PalletsAmount\":0,\"TaskDueDateStart\":\"0001-01-01T00:00:00\"," +
                "\"TaskDueDateFinish\":\"" +
                "2020-07-20T17:27:00" +
                "\",\"Status\":50,\"PercentFinishedActivities\":0,\"OrderDetails\":{\"OrderNo\":202023126,\"CustomerName\":\"Amazon\",\"CustomerNo\":30118,\"ReferenceIdCustomer1\":\"a\",\"ReferenceIdCustomer2\":null},\"TaskDetails\":{\"Description\":null,\"LoadingOrder\":0,\"ReferenceId1\":null,\"ReferenceId2\":null},\"Address\":{\"Name1\":null,\"Name2\":null,\"Street\":\"Stanka Vraza 11\",\"ZIP\":\"32000\",\"City\":\"Vukovar\",\"State\":null,\"Nation\":\"HR\",\"Longitude\":19.00919,\"Latitude\":45.3428,\"Note\":null},\"SwapInfoItem\":null,\"PalletExchange\":{\"ExchangeType\":0,\"PalletsAmount\":0,\"IsDPL\":false},\"DangerousGoods\":{\"IsGoodsDangerous\":false,\"ADRClass\":null,\"DangerousGoodsClassType\":0,\"UNNo\":null},\"Activities\":[{\"MandantId\":3,\"TaskId\":1228,\"ActivityId\":12,\"Name\":\"Driving to loading\",\"Description\":null,\"Started\":\"2020-06-02T12:46:48\",\"Finished\":\"2020-06-02T12:46:49\",\"Status\":2,\"Sequence\":0,\"CustomActivityId\":0,\"DeviceId\":null,\"DelayReasons\":null},{\"MandantId\":3,\"TaskId\":1228,\"ActivityId\":13,\"Name\":\"Waiting for loading\",\"Description\":null,\"Started\":\"2020-06-02T12:46:49\",\"Finished\":\"2020-06-02T12:46:56\",\"Status\":2,\"Sequence\":1,\"CustomActivityId\":0,\"DeviceId\":null,\"DelayReasons\":null},{\"MandantId\":3,\"TaskId\":1228,\"ActivityId\":14,\"Name\":\"Loading\",\"Description\":null,\"Started\":\"2020-06-02T12:46:56\",\"Finished\":\"2020-06-02T12:46:58\",\"Status\":2,\"Sequence\":2,\"CustomActivityId\":0,\"DeviceId\":null,\"DelayReasons\":null}],\"Contacts\":[{\"ContactType\":0,\"NumberType\":0,\"Name\":null,\"Number\":null}],\"Notes\":null,\"ChangedItems\":null},{\"MandantId\":3,\"MandantName\":\"Hegelmann Express GmbH\",\"TaskId\":1351,\"TaskChangeId\":3521,\"AbonaTransferNr\":\"\",\"PreviousTaskId\":0,\"NextTaskId\":1352,\"VehiclePreviousTaskId\":0,\"VehicleNextTaskId\":1352,\"ChangeReason\":0,\"ActionType\":0,\"OrderNo\":202026112,\"Description\":null,\"KundenName\":\"Amazon\",\"KundenNr\":30118,\"ReferenceIdCustomer1\":\"3234\",\"ReferenceIdCustomer2\":null,\"PalletsAmount\":0,\"TaskDueDateStart\":\"0001-01-01T00:00:00\"," +
                "\"TaskDueDateFinish\":\"" +
                "2020-07-20T17:27:00" +
                "\",\"Status\":0,\"PercentFinishedActivities\":0,\"OrderDetails\":{\"OrderNo\":202026112,\"CustomerName\":\"Amazon\",\"CustomerNo\":30118,\"ReferenceIdCustomer1\":\"3234\",\"ReferenceIdCustomer2\":null},\"TaskDetails\":{\"Description\":null,\"LoadingOrder\":1,\"ReferenceId1\":null,\"ReferenceId2\":null},\"Address\":{\"Name1\":\"In Time,\",\"Name2\":null,\"Street\":\"Wasserweg.19\",\"ZIP\":\"64521\",\"City\":\"Groß-Gerau,\",\"State\":null,\"Nation\":\"DE\",\"Longitude\":0,\"Latitude\":0,\"Note\":null},\"SwapInfoItem\":null,\"PalletExchange\":{\"ExchangeType\":0,\"PalletsAmount\":0,\"IsDPL\":false},\"DangerousGoods\":{\"IsGoodsDangerous\":false,\"ADRClass\":null,\"DangerousGoodsClassType\":0,\"UNNo\":null},\"Activities\":[{\"MandantId\":3,\"TaskId\":1351,\"ActivityId\":12,\"Name\":\"Driving to loading\",\"Description\":null,\"Started\":\"2020-06-25T12:54:40\",\"Finished\":\"2020-06-25T12:54:51\",\"Status\":2,\"Sequence\":0,\"CustomActivityId\":0,\"DeviceId\":null,\"DelayReasons\":null},{\"MandantId\":3,\"TaskId\":1351,\"ActivityId\":13,\"Name\":\"Waiting for loading\",\"Description\":null,\"Started\":\"2020-06-25T12:54:51\",\"Finished\":\"2020-06-25T12:54:54\",\"Status\":2,\"Sequence\":1,\"CustomActivityId\":0,\"DeviceId\":null,\"DelayReasons\":null},{\"MandantId\":3,\"TaskId\":1351,\"ActivityId\":14,\"Name\":\"Loading\",\"Description\":null,\"Started\":\"2020-06-25T12:54:54\",\"Finished\":\"2020-06-25T12:54:56\",\"Status\":2,\"Sequence\":2,\"CustomActivityId\":0,\"DeviceId\":null,\"DelayReasons\":null}],\"Contacts\":[{\"ContactType\":0,\"NumberType\":0,\"Name\":null,\"Number\":\"004915114744063\"}],\"Notes\":null,\"ChangedItems\":null},{\"MandantId\":3,\"MandantName\":\"Hegelmann Express GmbH\",\"TaskId\":1352,\"TaskChangeId\":3522,\"AbonaTransferNr\":\"\",\"PreviousTaskId\":1351,\"NextTaskId\":1357,\"VehiclePreviousTaskId\":1351,\"VehicleNextTaskId\":1357,\"ChangeReason\":0,\"ActionType\":0,\"OrderNo\":202026112,\"Description\":null,\"KundenName\":\"Amazon\",\"KundenNr\":30118,\"ReferenceIdCustomer1\":\"3234\",\"ReferenceIdCustomer2\":null,\"PalletsAmount\":0,\"TaskDueDateStart\":\"0001-01-01T00:00:00\"," +
                "\"TaskDueDateFinish\":\"" +
                "2020-07-20T17:47:00" +
                "\",\"Status\":0,\"PercentFinishedActivities\":0,\"OrderDetails\":{\"OrderNo\":202026112,\"CustomerName\":\"Amazon\",\"CustomerNo\":30118,\"ReferenceIdCustomer1\":\"3234\",\"ReferenceIdCustomer2\":null},\"TaskDetails\":{\"Description\":null,\"LoadingOrder\":2,\"ReferenceId1\":null,\"ReferenceId2\":null},\"Address\":{\"Name1\":\"Audi AG,\",\"Name2\":null,\"Street\":\"Hafenstrase, (49.194132, 9.221699)\",\"ZIP\":\"74172\",\"City\":\"Neckarsulm,\",\"State\":null,\"Nation\":\"DE\",\"Longitude\":0,\"Latitude\":0,\"Note\":null},\"SwapInfoItem\":null,\"PalletExchange\":{\"ExchangeType\":0,\"PalletsAmount\":0,\"IsDPL\":false},\"DangerousGoods\":{\"IsGoodsDangerous\":false,\"ADRClass\":null,\"DangerousGoodsClassType\":0,\"UNNo\":null},\"Activities\":[{\"MandantId\":3,\"TaskId\":1352,\"ActivityId\":12,\"Name\":\"Driving to loading\",\"Description\":null,\"Started\":\"2020-06-26T08:32:11\",\"Finished\":\"2020-06-26T08:32:48\",\"Status\":2,\"Sequence\":0,\"CustomActivityId\":0,\"DeviceId\":null,\"DelayReasons\":null},{\"MandantId\":3,\"TaskId\":1352,\"ActivityId\":13,\"Name\":\"Waiting for loading\",\"Description\":null,\"Started\":\"2020-06-26T08:32:48\",\"Finished\":\"2020-06-26T08:33:08\",\"Status\":2,\"Sequence\":1,\"CustomActivityId\":0,\"DeviceId\":null,\"DelayReasons\":null},{\"MandantId\":3,\"TaskId\":1352,\"ActivityId\":14,\"Name\":\"Loading\",\"Description\":null,\"Started\":\"2020-06-26T08:33:08\",\"Finished\":\"2020-06-26T08:36:37\",\"Status\":2,\"Sequence\":2,\"CustomActivityId\":0,\"DeviceId\":null,\"DelayReasons\":null}],\"Contacts\":[{\"ContactType\":0,\"NumberType\":0,\"Name\":null,\"Number\":\"004915114744063\"}],\"Notes\":null,\"ChangedItems\":null},{\"MandantId\":3,\"MandantName\":\"Hegelmann Express GmbH\",\"TaskId\":1354,\"TaskChangeId\":3524,\"AbonaTransferNr\":\"\",\"PreviousTaskId\":1357,\"NextTaskId\":0,\"VehiclePreviousTaskId\":1357,\"VehicleNextTaskId\":0,\"ChangeReason\":0,\"ActionType\":1,\"OrderNo\":202026112,\"Description\":null,\"KundenName\":\"Amazon\",\"KundenNr\":30118,\"ReferenceIdCustomer1\":\"3234\",\"ReferenceIdCustomer2\":null,\"PalletsAmount\":0,\"TaskDueDateStart\":\"0001-01-01T00:00:00\"," +
                "\"TaskDueDateFinish\":\"" +
                "2020-07-20T17:49:00" +
                "\",\"Status\":0,\"PercentFinishedActivities\":0,\"OrderDetails\":{\"OrderNo\":202026112,\"CustomerName\":\"Amazon\",\"CustomerNo\":30118,\"ReferenceIdCustomer1\":\"3234\",\"ReferenceIdCustomer2\":null},\"TaskDetails\":{\"Description\":null,\"LoadingOrder\":-4,\"ReferenceId1\":null,\"ReferenceId2\":null},\"Address\":{\"Name1\":\"Airex Ag\",\"Name2\":null,\"Street\":\"Industrie Nord 26\",\"ZIP\":\"5643\",\"City\":\"Sins\",\"State\":null,\"Nation\":\"CH\",\"Longitude\":0,\"Latitude\":0,\"Note\":null},\"SwapInfoItem\":null,\"PalletExchange\":{\"ExchangeType\":0,\"PalletsAmount\":0,\"IsDPL\":false},\"DangerousGoods\":{\"IsGoodsDangerous\":false,\"ADRClass\":null,\"DangerousGoodsClassType\":0,\"UNNo\":null},\"Activities\":[{\"MandantId\":3,\"TaskId\":1354,\"ActivityId\":15,\"Name\":\"Driving to unloading\",\"Description\":null,\"Started\":\"0001-01-01T00:00:00\",\"Finished\":\"0001-01-01T00:00:00\",\"Status\":0,\"Sequence\":0,\"CustomActivityId\":0,\"DeviceId\":null,\"DelayReasons\":null},{\"MandantId\":3,\"TaskId\":1354,\"ActivityId\":16,\"Name\":\"Waiting for unloading\",\"Description\":null,\"Started\":\"0001-01-01T00:00:00\",\"Finished\":\"0001-01-01T00:00:00\",\"Status\":0,\"Sequence\":1,\"CustomActivityId\":0,\"DeviceId\":null,\"DelayReasons\":null},{\"MandantId\":3,\"TaskId\":1354,\"ActivityId\":26,\"Name\":\"Send me an  image\",\"Description\":null,\"Started\":\"0001-01-01T00:00:00\",\"Finished\":\"0001-01-01T00:00:00\",\"Status\":0,\"Sequence\":2,\"CustomActivityId\":0,\"DeviceId\":null,\"DelayReasons\":null},{\"MandantId\":3,\"TaskId\":1354,\"ActivityId\":17,\"Name\":\"Unloading\",\"Description\":null,\"Started\":\"0001-01-01T00:00:00\",\"Finished\":\"0001-01-01T00:00:00\",\"Status\":0,\"Sequence\":3,\"CustomActivityId\":0,\"DeviceId\":null,\"DelayReasons\":null},{\"MandantId\":3,\"TaskId\":1354,\"ActivityId\":27,\"Name\":\"Unloading Departed\",\"Description\":null,\"Started\":\"0001-01-01T00:00:00\",\"Finished\":\"0001-01-01T00:00:00\",\"Status\":0,\"Sequence\":4,\"CustomActivityId\":0,\"DeviceId\":null,\"DelayReasons\":null}],\"Contacts\":[{\"ContactType\":0,\"NumberType\":0,\"Name\":null,\"Number\":\"004915114744063\"}],\"Notes\":null,\"ChangedItems\":null},{\"MandantId\":3,\"MandantName\":\"Hegelmann Express GmbH\",\"TaskId\":1357,\"TaskChangeId\":3527,\"AbonaTransferNr\":\"\",\"PreviousTaskId\":1352,\"NextTaskId\":1354,\"VehiclePreviousTaskId\":1352,\"VehicleNextTaskId\":1354,\"ChangeReason\":0,\"ActionType\":1,\"OrderNo\":202026112,\"Description\":null,\"KundenName\":\"Amazon\",\"KundenNr\":30118,\"ReferenceIdCustomer1\":\"3234\",\"ReferenceIdCustomer2\":null,\"PalletsAmount\":0,\"TaskDueDateStart\":\"0001-01-01T00:00:00\"," +
                "\"TaskDueDateFinish\":\"" +
                "2020-06-20T00:00:00" +
                "\",\"Status\":100,\"PercentFinishedActivities\":0,\"OrderDetails\":{\"OrderNo\":202026112,\"CustomerName\":\"Amazon\",\"CustomerNo\":30118,\"ReferenceIdCustomer1\":\"3234\",\"ReferenceIdCustomer2\":null},\"TaskDetails\":{\"Description\":null,\"LoadingOrder\":3,\"ReferenceId1\":null,\"ReferenceId2\":null},\"Address\":{\"Name1\":\"NAYMAR LOGISTICA\",\"Name2\":null,\"Street\":\"Pol.Ind. El Llano Av. De Europa s/n\",\"ZIP\":\"45360\",\"City\":\"Villarrubia de Santiago\",\"State\":null,\"Nation\":\"ES\",\"Longitude\":0,\"Latitude\":0,\"Note\":null},\"SwapInfoItem\":null,\"PalletExchange\":{\"ExchangeType\":0,\"PalletsAmount\":0,\"IsDPL\":false},\"DangerousGoods\":{\"IsGoodsDangerous\":false,\"ADRClass\":null,\"DangerousGoodsClassType\":0,\"UNNo\":null},\"Activities\":[{\"MandantId\":3,\"TaskId\":1357,\"ActivityId\":15,\"Name\":\"Driving to unloading\",\"Description\":null,\"Started\":\"2020-06-26T08:59:00\",\"Finished\":\"2020-06-26T08:59:03\",\"Status\":2,\"Sequence\":0,\"CustomActivityId\":0,\"DeviceId\":null,\"DelayReasons\":null},{\"MandantId\":3,\"TaskId\":1357,\"ActivityId\":16,\"Name\":\"Waiting for unloading\",\"Description\":null,\"Started\":\"2020-06-26T08:59:03\",\"Finished\":\"2020-06-26T08:59:19.85\",\"Status\":2,\"Sequence\":1,\"CustomActivityId\":0,\"DeviceId\":null,\"DelayReasons\":null},{\"MandantId\":3,\"TaskId\":1357,\"ActivityId\":26,\"Name\":\"Send me an  image\",\"Description\":null,\"Started\":\"2020-06-26T08:59:19\",\"Finished\":\"2020-07-13T14:34:17\",\"Status\":2,\"Sequence\":2,\"CustomActivityId\":0,\"DeviceId\":null,\"DelayReasons\":null},{\"MandantId\":3,\"TaskId\":1357,\"ActivityId\":17,\"Name\":\"Unloading\",\"Description\":null,\"Started\":\"2020-07-13T14:34:17\",\"Finished\":\"2020-07-13T14:34:21\",\"Status\":2,\"Sequence\":3,\"CustomActivityId\":0,\"DeviceId\":null,\"DelayReasons\":null},{\"MandantId\":3,\"TaskId\":1357,\"ActivityId\":27,\"Name\":\"Unloading Departed\",\"Description\":null,\"Started\":\"2020-07-13T14:34:21\",\"Finished\":\"2020-07-13T14:43:35\",\"Status\":2,\"Sequence\":4,\"CustomActivityId\":0,\"DeviceId\":null,\"DelayReasons\":null}],\"Contacts\":[{\"ContactType\":0,\"NumberType\":0,\"Name\":null,\"Number\":\"004915114744063\"}],\"Notes\":null,\"ChangedItems\":null}],\"AllTaskCommItem\":[],\"HttpResponseMessage\":null,\"TransportAuftragOid\":0,\"AllAppFileInterchangeItem\":null,\"LogText\":null,\"AllDocumentCommItem\":[],\"DelayReasons\":null}"
    //     * get future time based on random, Constants.TEST_TIME_QUOTES,
    //     * Constants.REPEAT_TIME,
    //     * Constants.REPEAT_COUNT
    //     * @return String time format: yyyy-MM-dd'T'HH:mm:ss
    //     */
    //    @NotNull
    //    private String getTestTime() {
    //        Calendar calendar = Calendar.getInstance();
    //        calendar.setTimeInMillis(System.currentTimeMillis());
    //        calendar.add(Calendar.MINUTE, + (TextSecurePreferences.getNotificationTime())); //future task
    //        Random r = new Random(System.currentTimeMillis());
    //        calendar.add(Calendar.MINUTE, r.nextInt(Constants.TEST_TIME_QUOTES) +2);
    //        String format = "yyyy-MM-dd'T'HH:mm:ss";
    //        final SimpleDateFormat sdf = new SimpleDateFormat(format);
    //
    //        return sdf.format(calendar.getTime());
    //    }
}