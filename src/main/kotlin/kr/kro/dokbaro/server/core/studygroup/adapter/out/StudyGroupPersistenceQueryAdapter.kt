package kr.kro.dokbaro.server.core.studygroup.adapter.out

import kr.kro.dokbaro.server.common.annotation.PersistenceAdapter
import kr.kro.dokbaro.server.common.dto.option.PageOption
import kr.kro.dokbaro.server.core.studygroup.adapter.out.persistence.repository.jooq.StudyGroupQueryRepository
import kr.kro.dokbaro.server.core.studygroup.application.port.out.CountStudyGroupPort
import kr.kro.dokbaro.server.core.studygroup.application.port.out.ReadStudyGroupCollectionPort
import kr.kro.dokbaro.server.core.studygroup.application.port.out.ReadStudyGroupDetailPort
import kr.kro.dokbaro.server.core.studygroup.application.port.out.ReadStudyGroupMemberCollectionPort
import kr.kro.dokbaro.server.core.studygroup.application.port.out.dto.CountStudyGroupCondition
import kr.kro.dokbaro.server.core.studygroup.query.StudyGroupDetail
import kr.kro.dokbaro.server.core.studygroup.query.StudyGroupMemberResult
import kr.kro.dokbaro.server.core.studygroup.query.StudyGroupSummary
import kr.kro.dokbaro.server.core.studygroup.query.sort.MyStudyGroupSortKeyword

@PersistenceAdapter
class StudyGroupPersistenceQueryAdapter(
	private val studyGroupQueryRepository: StudyGroupQueryRepository,
) : ReadStudyGroupCollectionPort,
	ReadStudyGroupMemberCollectionPort,
	ReadStudyGroupDetailPort,
	CountStudyGroupPort {
	override fun findAllByStudyMemberId(
		memberId: Long,
		pageOption: PageOption<MyStudyGroupSortKeyword>,
	): Collection<StudyGroupSummary> = studyGroupQueryRepository.findAllByStudyMemberId(memberId, pageOption)

	override fun findAllStudyGroupMembers(id: Long): Collection<StudyGroupMemberResult> =
		studyGroupQueryRepository.findAllStudyGroupMembers(id)

	override fun findStudyGroupDetailBy(id: Long): StudyGroupDetail? = studyGroupQueryRepository.findDetailBy(id)

	override fun countBy(condition: CountStudyGroupCondition): Long = studyGroupQueryRepository.countBy(condition)
}