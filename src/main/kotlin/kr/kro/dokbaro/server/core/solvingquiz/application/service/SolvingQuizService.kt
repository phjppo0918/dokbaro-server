package kr.kro.dokbaro.server.core.solvingquiz.application.service

import kr.kro.dokbaro.server.core.bookquiz.application.port.input.FindBookQuizByQuestionIdUseCase
import kr.kro.dokbaro.server.core.bookquiz.domain.AnswerSheet
import kr.kro.dokbaro.server.core.bookquiz.domain.BookQuiz
import kr.kro.dokbaro.server.core.bookquiz.domain.GradeResult
import kr.kro.dokbaro.server.core.bookquiz.domain.QuestionAnswer
import kr.kro.dokbaro.server.core.member.application.port.input.query.FindCertificatedMemberUseCase
import kr.kro.dokbaro.server.core.solvingquiz.application.port.input.SolveQuestionUseCase
import kr.kro.dokbaro.server.core.solvingquiz.application.port.input.StartSolvingQuizUseCase
import kr.kro.dokbaro.server.core.solvingquiz.application.port.input.dto.SolveQuestionCommand
import kr.kro.dokbaro.server.core.solvingquiz.application.port.input.dto.StartSolvingQuizCommand
import kr.kro.dokbaro.server.core.solvingquiz.application.port.out.InsertSolvingQuizPort
import kr.kro.dokbaro.server.core.solvingquiz.application.port.out.LoadSolvingQuizPort
import kr.kro.dokbaro.server.core.solvingquiz.application.port.out.UpdateSolvingQuizPort
import kr.kro.dokbaro.server.core.solvingquiz.application.service.exception.NotFoundSolvingQuizException
import kr.kro.dokbaro.server.core.solvingquiz.domain.SolvingQuiz
import kr.kro.dokbaro.server.core.solvingquiz.query.SolveResult
import org.springframework.stereotype.Service

@Service
class SolvingQuizService(
	private val findCertificatedMemberUseCase: FindCertificatedMemberUseCase,
	private val insertSolvingQuizPort: InsertSolvingQuizPort,
	private val loadSolvingQuizPort: LoadSolvingQuizPort,
	private val updateSolvingQuizPort: UpdateSolvingQuizPort,
	private val findBookQuizByQuestionIdUseCase: FindBookQuizByQuestionIdUseCase,
) : StartSolvingQuizUseCase,
	SolveQuestionUseCase {
	override fun start(command: StartSolvingQuizCommand): Long {
		val memberId = findCertificatedMemberUseCase.getByCertificationId(command.authId).id

		return insertSolvingQuizPort.insert(SolvingQuiz(memberId, command.quizId))
	}

	override fun solve(command: SolveQuestionCommand): SolveResult {
		val solvingQuiz: SolvingQuiz =
			loadSolvingQuizPort.findById(command.solvingQuizId)
				?: throw NotFoundSolvingQuizException(command.solvingQuizId)
		val sheet = AnswerSheet(command.answers)

		solvingQuiz.addSheet(command.questionId, sheet)
		updateSolvingQuizPort.update(solvingQuiz)

		val bookQuiz: BookQuiz = findBookQuizByQuestionIdUseCase.findByQuestionId(command.questionId)

		val gradeResult: GradeResult = bookQuiz.grade(command.questionId, sheet)
		val answer: QuestionAnswer = bookQuiz.getAnswer(command.questionId)

		return SolveResult(
			solvingQuizId = solvingQuiz.id,
			playerId = solvingQuiz.playerId,
			quizId = solvingQuiz.quizId,
			questionId = command.questionId,
			correct = gradeResult.correct,
			correctAnswer = answer.correctAnswer(),
			answerExplanationContent = answer.explanationContent,
			answerExplanationImages = answer.explanationImages,
		)
	}
}