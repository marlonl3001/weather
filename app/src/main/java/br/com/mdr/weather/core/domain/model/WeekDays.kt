package br.com.mdr.weather.core.domain.model

enum class WeekDays(val intDay: Int, val formattedDay: String) {
    DOM(1, "Dom."),
    SEG(2, "Seg."),
    TER(3, "Ter."),
    QUA(4, "Qua."),
    QUI(5, "Qui."),
    SEX(6, "Sex."),
    SAB(7, "Sáb."),
    HOJ(0, "Hoje")
}