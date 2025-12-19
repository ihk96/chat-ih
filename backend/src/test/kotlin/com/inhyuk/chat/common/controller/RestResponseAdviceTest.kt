package com.inhyuk.chat.common.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import io.kotest.core.spec.style.BehaviorSpec
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class TestController {
    @GetMapping("/test/default")
    fun defaultCode(): String = "ok"

    @GetMapping("/test/rest-response")
    fun restResponse(): RestResponse<String> = RestResponse.success("ok", "SUCCESS")
    
    @GetMapping("/test/object")
    fun objectResponse(): Map<String, String> = mapOf("key" to "value")
}

class RestResponseAdviceTest : BehaviorSpec({
    val mapper = jacksonObjectMapper()
    val advice = RestResponseAdvice(mapper)
    val mockMvc = MockMvcBuilders.standaloneSetup(TestController())
        .setControllerAdvice(advice)
        .build()

    Given("A controller with RestResponseAdvice") {
        When("Calling endpoint returning raw string") {
            val result = mockMvc.perform(get("/test/default"))
            
            Then("It should be wrapped with default code '0'") {
                result.andExpect(status().isOk)
                    .andExpect(jsonPath("$.code").value("0"))
                    .andExpect(jsonPath("$.data").value("ok"))
            }
        }

        When("Calling endpoint returning RestResponse") {
            val result = mockMvc.perform(get("/test/rest-response"))
            
            Then("It should have the code from RestResponse") {
                result.andExpect(status().isOk)
                    .andExpect(jsonPath("$.code").value("SUCCESS"))
                    .andExpect(jsonPath("$.data").value("ok"))
            }
        }
        
        When("Calling endpoint returning object") {
            val result = mockMvc.perform(get("/test/object"))
            
            Then("It should be wrapped with default code '0'") {
                result.andExpect(status().isOk)
                    .andExpect(jsonPath("$.code").value("0"))
                    .andExpect(jsonPath("$.data.key").value("value"))
            }
        }
    }
})
