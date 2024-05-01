import java.time.Duration
import java.time.LocalDateTime
import kotlin.math.ceil

class HachiSalaryCalculator(
    private val startTime: LocalDateTime,	// 労働開始時間
    private val endTime: LocalDateTime,		// 労働終了時間
    private val breakTime: Double,			// 休憩時間
    private val isHoliday: Boolean,			// 休日フラグ
    private val specialWage: Int,		// 特殊時給
    private val isSpecialWage: Boolean,		// 特殊時給フラグ
) {
    // 将来的に設定画面で任意で変更できるようにする
    private val baseWage: Int = 1100				// 基本時給
    private val holidayWage: Int = 1150				// 休日時給
    private var wage: Int = calculateWage()						// 計算時給

    private var isNightWork: Boolean = false			// 夜勤の有無フラグ
    private var isOverWork: Boolean = false				// 残業ありフラグ
    private var is22UnderOverWork: Boolean = false		// 22時前に残業ありフラグ

    private val normalWork: Double = 8.0				// 法廷内労働時間
    private val nightWorkStartTime: Double = 22.0			// 夜勤開始時間

    private val singleMultiplier: Double = 1.25			// 最初の割増
    private val doubleMultiplier: Double = 1.5			// ２回目の割増


    private val startTimeHour = startTime.hour.toDouble() + (startTime.minute.toDouble() / 60) 	// HH:mm -> H.m １０進化
    private val endTimeHour = endTime.hour.toDouble() + (endTime.minute.toDouble() / 60)		// HH:mm -> H.m １０進化

    private val dayWorkTime = nightWorkStartTime - startTimeHour - breakTime	// 22時までの労働時間
    private val nightWorkTime = endTimeHour - nightWorkStartTime				// 22時以降の労働時間
    private val workTime = endTimeHour - startTimeHour - breakTime				// １日の労働時間

    private var daySalary = 0

    init {
        isNightWork = calculateNightWork()
        isOverWork = calculateOverWork()
        is22UnderOverWork = calculate22UnderOverWork()
        daySalary = if (isNightWork) if (isOverWork) if (is22UnderOverWork) calcOverAfterNightSalary() else calcNightAfterOverSalary() else calcNightSalary() else if (isOverWork) calcOverWorkSalary() else calcNormalSalary()
    }

    // 時給のセット
    private fun calculateWage(): Int {
        return if(isSpecialWage) specialWage else if(isHoliday) holidayWage else baseWage
    }

    // 夜勤フラグの初期化
    private fun calculateNightWork(): Boolean {
        val hour: Double = endTime.hour.toDouble()
        val min: Double = endTime.minute.toDouble() / 60
        val total: Double = hour + min
        return  total > nightWorkStartTime
    }

    // 残業フラグの初期化
    private fun calculateOverWork(): Boolean {
        val workTimeHour = (Duration.between(startTime, endTime).toMinutes()).toDouble() / 60
        return workTimeHour - breakTime > normalWork
    }

    // 22時より前の残業フラグの初期化
    private fun calculate22UnderOverWork(): Boolean {
        val startTimeHour = startTime.hour.toDouble() + (startTime.minute.toDouble() / 60)
        val workTimeHour = nightWorkStartTime - startTimeHour
        return workTimeHour - breakTime > normalWork
    }

    // 日給割り出しメソッド
    // 計算メソッド
    fun calculateSalary(): Int {
        return daySalary
    }
    // 手当なしの日給
    private fun calcNormalSalary(): Int {
        val salary = workTime * wage
        return ceil(salary).toInt()
    }
    // 残業手当のみ
    private fun calcOverWorkSalary(): Int {
        val salary = (normalWork * wage.toDouble()) + ((workTime - normalWork) * wage.toDouble() * singleMultiplier)
        return ceil(salary).toInt()
    }
    // 夜勤手当のみ
    private fun calcNightSalary(): Int {
        val salary = (dayWorkTime * wage.toDouble()) + (nightWorkTime * wage.toDouble() * singleMultiplier)
        return ceil(salary).toInt()
    }
    // 残業して夜勤
    private fun calcOverAfterNightSalary(): Int {
        val salary = (normalWork * wage.toDouble()) + ((dayWorkTime - normalWork) * wage.toDouble() * singleMultiplier) + (nightWorkTime * wage.toDouble() * doubleMultiplier)
        return ceil(salary).toInt()
    }
    // 夜勤して残業
    private fun calcNightAfterOverSalary(): Int {
        val salary = (dayWorkTime * wage.toDouble()) + ((normalWork - dayWorkTime) * wage.toDouble() * singleMultiplier) + ((workTime - normalWork) * wage.toDouble() * doubleMultiplier)
        return ceil(salary).toInt()
    }

}