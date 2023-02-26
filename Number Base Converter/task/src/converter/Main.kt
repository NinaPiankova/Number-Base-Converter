package converter

import java.math.BigDecimal
import java.math.BigInteger


fun getKey(map: MutableMap<Char, Int>, target: Int): Char {
    for ((key, value) in map)
    {
        if (target == value) {
            return key
        }
    }
    return ' '
}

fun inPower(number: Int, pw: Int): BigInteger {
    var result = BigInteger.ONE
    for (i in 0 until  pw) {
        result = result * number.toBigInteger()
    }
    return result
}
fun convertToDecimal(sourseBase: Int, convertString: String, dictionary: MutableMap<Char, Int>): BigInteger {
    var result = BigInteger.ZERO
    var pw = convertString.length - 1
    for (i in 0 until convertString.length) {
        var number: BigInteger = dictionary[convertString[i]]!!.toBigInteger() // a = 10
        var sourseInPower = inPower(sourseBase, pw) //8 в степени
        result = result + number.multiply(sourseInPower)
        pw--
    }
    return result
}


fun convertNumber(sourseBase: Int, targetBase: Int, convertString: String,
                  dictionary: MutableMap<Char, Int>):String {
    var number = BigInteger.ZERO //28 2   d0.iq6il36icl8k1laq9op04ghe d0.iq6il36icl8k1laq
    var str = ""
    var remainder = BigInteger.ZERO

    if(convertString.equals("0")) return "0"
    if(convertString.contains(".")) {
        var firstPart = ""
        var secondPart = ""

        val decArray = convertString.split('.')
        val unit = decArray[0]
        var fraction = decArray[1]

        if (unit.equals("0")) firstPart = "0"
        else {
            number = convertToDecimal (sourseBase,unit.lowercase(),dictionary)
            firstPart = GetWholeNumber (number,targetBase,dictionary)
        }

        secondPart = convertFraction(sourseBase,targetBase, fraction.lowercase(),dictionary)

        str = firstPart + "." + secondPart
    }
    else {
        number = convertToDecimal (sourseBase,convertString.lowercase(),dictionary)
        str = GetWholeNumber (number,targetBase,dictionary)
    }
    return str

}

fun convertFraction(sourseBase: Int, targetBase: Int, convertString: String, dictionary: MutableMap<Char, Int>)
: String {
    //println("convertFraction" + convertString)
    var number: BigDecimal = ConvertFractionToDec (sourseBase, convertString, dictionary)
        .setScale(5,BigDecimal.ROUND_FLOOR)
    var size = 5
    var strNum = ""
    var multNumber = number

    while (size > 0) {
        multNumber = multNumber * targetBase.toBigDecimal()
        var result = BigDecimal.ONE
        if (multNumber > targetBase.toBigDecimal()) {
            multNumber = multNumber - targetBase.toBigDecimal()
            result = multNumber.setScale(0,BigDecimal.ROUND_FLOOR)

        }
        else {

            result = multNumber.setScale(0,BigDecimal.ROUND_FLOOR)
            multNumber = multNumber - result
        }
        strNum = strNum + getKey(dictionary, result.toInt())
        size --
    }

    return strNum
}

fun ConvertFractionToDec(sourseBase: Int, convertString: String, dictionary: MutableMap<Char, Int>): BigDecimal {
    //val str = convertString.substring(0,5)
    val size = convertString.length
    var sum = 0.0
    for (i in 1..size)
    {
        val basePower = inPower(sourseBase,i).toInt()
        val devResult = 1.0 / basePower
        if (devResult.isNaN()||devResult.isInfinite()) break
        val mult = dictionary[convertString[i - 1]]!!.toInt()
        val result = devResult * mult
        sum = sum + result
        //println("devResult" + devResult)
    }
    //println("ConvertFractionToDec" + sum)
    return sum.toBigDecimal().setScale(5,BigDecimal.ROUND_HALF_DOWN)
}

fun GetWholeNumber(num: BigInteger, targetBase: Int, dictionary: MutableMap<Char, Int>): String {

    var number = num
    var remainder = BigInteger.ZERO
    var str = ""
    while (number > BigInteger.ZERO) {
        remainder = number.mod(targetBase.toBigInteger())
        number = number.divide(targetBase.toBigInteger())
        str = str + getKey(dictionary, remainder.toInt())
    }
    return str.reversed()
}

fun main() {

    var input = ""
    var sourseBase = 0
    var targetBase = 0
    var isFirst = true
    val dictionary = mutableMapOf<Char, Int>()
    var num = 0
    for (i in 0..9) {
        dictionary[Character.forDigit(i, 10)] = num
        num++
    }
    for (i in 'a'..'z') {
        dictionary[i] = num
        num++
    }
    
    while (true) {
        if (isFirst) {
            print("Enter two numbers in format: {source base} {target base} (To quit type /exit) ")
            input = readLine()!!
            if (input.equals("/exit")) break
            if (input.equals("/back")) continue
            val array = input.split(' ')
            sourseBase = array[0].toInt()
            targetBase = array[1].toInt()
            isFirst = false
            continue
        } else {
            print("Enter number in base $sourseBase to convert to base $targetBase (To go back type /back) ")
            var convertString = readLine()!!
            if (convertString.equals("/exit")) break
            if (convertString.equals("/back")) {
                println()
                isFirst = true
                continue
            }
            val res: String = convertNumber(sourseBase, targetBase, convertString, dictionary)
            println("Conversion result: $res")
            println()
        }
    }
}
