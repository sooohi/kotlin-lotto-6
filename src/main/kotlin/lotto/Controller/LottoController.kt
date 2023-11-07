package lotto.Controller

import camp.nextstep.edu.missionutils.Randoms
import camp.nextstep.edu.missionutils.Console

enum class LottoPrize(val sameCount: Int, val prizeMoney: Int, val prizeName: String) {
    threeSame(3, 5000, "3개 일치"),
    fourSame(4, 50000, "4개 일치"),
    fiveSame(5, 1500000, "5개 일치"),
    fiveSamePlusBonus(5, 30000000, "5개 일치, 보너스 볼 일치"),
    sixSame(6, 2000000000, "6개 일치")
}

class LottoController {
    fun startGame() {
        try {
            val inputMoney = lottoMoneyInput()
            val count = lottoCnt(inputMoney)
            val lottoList = lottoNumberLimit(count)
            lottoNumberPrint(count, lottoList)

            val lottoNumber = lottoNumberChoose()
            val bonusNumber = lottoNumberBonus()

            val result = lottoNumberCheck(lottoList, lottoNumber, bonusNumber)
            lottoResultPrint(result, count)
        } catch (e: IllegalArgumentException) {
            println("[ERROR] ${e.message}")
        }
    }

    fun lottoMoneyInput(): Int {
        println("구입금액을 입력해 주세요.")
        val inputMoney = Console.readLine().toInt()
        if (inputMoney % 1000 != 0) {
            throw IllegalArgumentException("1000원 단위로 입력해 주세요.")
        }
        return inputMoney
    }

    fun lottoCnt(inputMoney: Int): Int {
        return inputMoney / 1000
    }

    fun lottoNumberLimit(count: Int): List<List<Int>> {
        val comLottoList = mutableListOf<List<Int>>()
        repeat(count) {
            val numbers = Randoms.pickUniqueNumbersInRange(1, 45, 6)
            comLottoList.add(numbers)
        }
        return comLottoList
    }

    fun lottoNumberPrint(lottoCount: Int, lottoList: List<List<Int>>) {
        println("$lottoCount 개를 구매했습니다.")
        lottoList.forEach { lotto ->
            println(lotto.sorted())
        }
    }

    fun lottoNumberChoose(): List<Int> {
        println("당첨 번호를 입력해 주세요.")
        val lottoNumber = Console.readLine().split(",").map { it.toInt() }
        if (lottoNumber.toSet().size != 6) {
            throw IllegalArgumentException("중복되는 번호가 있습니다.")
        }
        if (lottoNumber.size != 6) {
            throw IllegalArgumentException("6개의 숫자를 입력하세요.")
        }
        return lottoNumber
    }

    fun lottoNumberBonus(): Int {
        println("보너스 번호를 입력해 주세요.")
        return Console.readLine().toInt()
    }

    fun lottoNumberCheck(lottoList: List<List<Int>>, comNumber: List<Int>, bonusNumber: Int): Map<String, Int> {
        val lottoMoneyList = mutableMapOf(
            LottoPrize.threeSame.prizeName to 0,
            LottoPrize.fourSame.prizeName to 0,
            LottoPrize.fiveSame.prizeName to 0,
            LottoPrize.fiveSamePlusBonus.prizeName to 0,
            LottoPrize.sixSame.prizeName to 0
        )
        for (lotto in lottoList) {
            val sameNumber = lotto.filter { it in comNumber }.size
            when (sameNumber) {
                LottoPrize.threeSame.sameCount -> lottoMoneyList[LottoPrize.threeSame.prizeName] =
                    lottoMoneyList.getValue(LottoPrize.threeSame.prizeName) + 1

                LottoPrize.fourSame.sameCount -> lottoMoneyList[LottoPrize.fourSame.prizeName] =
                    lottoMoneyList.getValue(LottoPrize.fourSame.prizeName) + 1

                LottoPrize.fiveSame.sameCount -> {
                    if (lotto.contains(bonusNumber)) {
                        lottoMoneyList[LottoPrize.fiveSamePlusBonus.prizeName] =
                            lottoMoneyList.getValue(LottoPrize.fiveSamePlusBonus.prizeName) + 1
                    } else {
                        lottoMoneyList[LottoPrize.fiveSame.prizeName] =
                            lottoMoneyList.getValue(LottoPrize.fiveSame.prizeName) + 1
                    }
                }

                LottoPrize.sixSame.sameCount -> lottoMoneyList[LottoPrize.sixSame.prizeName] =
                    lottoMoneyList.getValue(LottoPrize.sixSame.prizeName) + 1
            }
        }
        return lottoMoneyList
    }

    fun lottoResultPrint(result: Map<String, Int>, lottoCount: Int) {
        println("당첨 통계")
        println("---")
        var totalPrize = 0
        val prizeMoney = mapOf(
            "3개 일치" to 5000,
            "4개 일치" to 50000,
            "5개 일치" to 1500000,
            "5개 일치, 보너스 볼 일치" to 30000000,
            "6개 일치" to 2000000000
        )
        for ((key, value) in result) {
            val prize = when (key) {
                "5개 일치" -> if (result[LottoPrize.fiveSamePlusBonus.prizeName] == 0) "1,500,000원" else "30,000,000원"
                else -> "${prizeMoney[key]}원"
            }
            println("$key ($prize) - ${value}개")
            totalPrize += if (key == "5개 일치") {
                if (result[LottoPrize.fiveSamePlusBonus.prizeName] == 0) prizeMoney["5개 일치"]!! * value else prizeMoney["5개 일치, 보너스 볼 일치"]!! * value
            } else {
                prizeMoney[key]!! * value
            }
        }
        val inputMoney = lottoCount * 1000
        val rateOfReturn = ((totalPrize - inputMoney) / inputMoney.toDouble() * 100).coerceAtLeast(0.0)
        println("총 수익률은 ${"%.1f".format(rateOfReturn)}%입니다.")
    }
}