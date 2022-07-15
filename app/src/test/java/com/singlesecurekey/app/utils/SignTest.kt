package com.singlesecurekey.app.utils

import junit.framework.TestCase

class SignTest : TestCase() {

    fun testGetHash() {

        val TEST  = "TEST"
        val a = Sign().getHash(TEST)
        assertEquals("e/qVpoiSTEfH0iOB8gzJJvUkvqyxP4TiA9S9jLa6L86BxXpfBZvz1QmSZIe96SWzvO4GNeT3uuugVOXbppayvw==",a)
    }

    fun test2(){

        val pattern = "JFHrLn+xl3BtCIHf3Njo+hKvnfjY1eTmb3hGPTGvWkivdsoT+kuif3AL5dmPISLU1e+QE7kw72xIExiyS09ukg=="
        val TEST1 = "{\"authRequestId\":\"e3d6a377-ede9-450d-bf36-8059465eee9f\",\"customerId\":\"e53a1785-9594-4b72-aa02-427ea4123a3b\",\"approved\":true}85169DA7F4CA88F8B50B0DD3C771E1E4C3B798F19CF16FEC73ADB72B3FA6201EA0F677974345FF9F945C93FC05DBCF92B66F6830245DE25DF47A85A5F4864EB127487826"
        val a = Sign().getHash(TEST1)
        assertEquals(pattern,a)

        val TEST2 = "{\"authRequestId\":\"e3d6a377-ede9-450d-bf36-8059465eee9f\",\"customerId\":\"e53a1785-9594-4b72-aa02-427ea4123a3b\",\"approved\":true}"+"85169DA7F4CA88F8B50B0DD3C771E1E4C3B798F19CF16FEC73ADB72B3FA6201EA0F677974345FF9F945C93FC05DBCF92B66F6830245DE25DF47A85A5F4864EB127487826"
        val a1 = Sign().getHash(TEST2)
        assertEquals(pattern,a1)
    }

}