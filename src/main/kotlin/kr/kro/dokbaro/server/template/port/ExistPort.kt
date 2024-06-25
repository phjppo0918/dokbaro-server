package kr.kro.dokbaro.server.template.port

interface ExistPort<T> {
	fun existBy(t: T): Boolean

	fun notExistBy(t: T): Boolean = !existBy(t)
}