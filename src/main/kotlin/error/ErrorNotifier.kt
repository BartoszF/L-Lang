package pl.bfelis.fc93.language.error

interface ErrorObserver {
    fun onErrors(errors: List<GeneralLException>)
}

class ErrorNotifier {
    private val errors: MutableList<GeneralLException> = mutableListOf()
    private val observers: MutableList<ErrorObserver> = mutableListOf()

    fun register(observer: ErrorObserver) {
        observers.add(observer)
    }

    fun error(error: GeneralLException) {
        errors.add(error)
    }

    fun notifyErrors() {
        observers.forEach { it.onErrors(errors) }
    }
}
