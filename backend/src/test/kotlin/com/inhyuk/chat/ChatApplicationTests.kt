package com.inhyuk.chat

import io.kotest.core.spec.style.FunSpec
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ChatApplicationTests : FunSpec({

    test("컨텍스트 로드 테스트"){
        print("안녕")
    }

})
