package kr.kro.dokbaro.server.core.studygroup.adapter.out.persistence.repository.jooq

import kr.kro.dokbaro.server.common.dto.option.PageOption
import kr.kro.dokbaro.server.common.dto.option.SortDirection
import kr.kro.dokbaro.server.core.studygroup.adapter.out.persistence.entity.jooq.StudyGroupMapper
import kr.kro.dokbaro.server.core.studygroup.application.port.out.dto.CountStudyGroupCondition
import kr.kro.dokbaro.server.core.studygroup.application.port.out.dto.FindStudyGroupCondition
import kr.kro.dokbaro.server.core.studygroup.domain.StudyGroup
import kr.kro.dokbaro.server.core.studygroup.query.StudyGroupDetail
import kr.kro.dokbaro.server.core.studygroup.query.StudyGroupMemberResult
import kr.kro.dokbaro.server.core.studygroup.query.StudyGroupSummary
import kr.kro.dokbaro.server.core.studygroup.query.sort.MyStudyGroupSortKeyword
import org.jooq.Condition
import org.jooq.DSLContext
import org.jooq.OrderField
import org.jooq.Record
import org.jooq.Result
import org.jooq.generated.tables.JMember.MEMBER
import org.jooq.generated.tables.JStudyGroup.STUDY_GROUP
import org.jooq.generated.tables.JStudyGroupMember.STUDY_GROUP_MEMBER
import org.jooq.generated.tables.records.StudyGroupRecord
import org.jooq.impl.DSL
import org.jooq.impl.DSL.field
import org.jooq.impl.DSL.select
import org.springframework.stereotype.Repository

@Repository
class StudyGroupQueryRepository(
	private val dslContext: DSLContext,
	private val studyGroupMapper: StudyGroupMapper,
) {
	fun findAllByStudyMemberId(
		memberId: Long,
		pageOption: PageOption<MyStudyGroupSortKeyword>,
	): Collection<StudyGroupSummary> {
		val record: Result<out Record> =
			dslContext
				.select(
					STUDY_GROUP.ID,
					STUDY_GROUP.NAME,
					STUDY_GROUP.PROFILE_IMAGE_URL,
					field(
						dslContext
							.selectCount()
							.from(STUDY_GROUP_MEMBER)
							.where(STUDY_GROUP_MEMBER.STUDY_GROUP_ID.eq(STUDY_GROUP.ID)),
					).`as`(StudyGroupRecordFieldName.STUDY_MEMBER_COUNT),
					MEMBER.ID.`as`(StudyGroupRecordFieldName.STUDY_LEADER_ID),
					MEMBER.NICKNAME.`as`(StudyGroupRecordFieldName.STUDY_LEADER_NAME),
				).from(STUDY_GROUP)
				// leader
				.join(STUDY_GROUP_MEMBER)
				.on(
					STUDY_GROUP_MEMBER.STUDY_GROUP_ID
						.eq(STUDY_GROUP.ID)
						.and(STUDY_GROUP_MEMBER.MEMBER_ROLE.eq("LEADER")),
				).join(MEMBER)
				.on(MEMBER.ID.eq(STUDY_GROUP_MEMBER.MEMBER_ID))
				.where(
					STUDY_GROUP.ID
						.`in`(
							select(
								STUDY_GROUP_MEMBER.STUDY_GROUP_ID,
							).from(STUDY_GROUP_MEMBER)
								.where(STUDY_GROUP_MEMBER.MEMBER_ID.eq(memberId)),
						).and(STUDY_GROUP.DELETED.eq(false)),
				).orderBy(toOrderByQuery(pageOption), STUDY_GROUP.ID)
				.offset(pageOption.offset)
				.limit(pageOption.limit)
				.fetch()

		return studyGroupMapper.toStudyGroupSummary(record)
	}

	fun toOrderByQuery(pageOption: PageOption<MyStudyGroupSortKeyword>): OrderField<out Any> {
		val query =
			when (pageOption.sort) {
				MyStudyGroupSortKeyword.CREATED_AT -> STUDY_GROUP.CREATED_AT
				MyStudyGroupSortKeyword.JOINED_AT -> STUDY_GROUP_MEMBER.CREATED_AT
				MyStudyGroupSortKeyword.NAME -> STUDY_GROUP.NAME
			}

		if (pageOption.direction == SortDirection.DESC) {
			return query.desc()
		}

		return query
	}

	fun findAllStudyGroupMembers(id: Long): Collection<StudyGroupMemberResult> {
		val record: Result<out Record> =
			dslContext
				.select(
					STUDY_GROUP_MEMBER.ID,
					STUDY_GROUP_MEMBER.MEMBER_ID,
					STUDY_GROUP_MEMBER.STUDY_GROUP_ID,
					MEMBER.NICKNAME,
					STUDY_GROUP_MEMBER.MEMBER_ROLE,
				).from(STUDY_GROUP_MEMBER)
				.join(MEMBER)
				.on(MEMBER.ID.eq(STUDY_GROUP_MEMBER.MEMBER_ID))
				.where(STUDY_GROUP_MEMBER.STUDY_GROUP_ID.eq(id))
				.fetch()

		return studyGroupMapper.toStudyGroupMemberResult(record)
	}

	fun findDetailBy(condition: FindStudyGroupCondition): StudyGroupDetail? {
		val record: Map<StudyGroupRecord, Result<out Record>> =
			dslContext
				.select(
					STUDY_GROUP.ID,
					STUDY_GROUP.INVITE_CODE,
					STUDY_GROUP.NAME,
					STUDY_GROUP.INTRODUCTION,
					STUDY_GROUP.PROFILE_IMAGE_URL,
					STUDY_GROUP_MEMBER.MEMBER_ID,
					STUDY_GROUP_MEMBER.MEMBER_ROLE,
					MEMBER.NICKNAME,
					MEMBER.PROFILE_IMAGE_URL,
				).from(STUDY_GROUP)
				.join(STUDY_GROUP_MEMBER)
				.on(STUDY_GROUP_MEMBER.STUDY_GROUP_ID.eq(STUDY_GROUP.ID))
				.join(MEMBER)
				.on(MEMBER.ID.eq(STUDY_GROUP_MEMBER.MEMBER_ID))
				.where(buildDetailCondition(condition).and(STUDY_GROUP.DELETED.eq(false)))
				.fetchGroups(STUDY_GROUP)

		return studyGroupMapper.toStudyGroupDetail(record)
	}

	private fun buildDetailCondition(condition: FindStudyGroupCondition): Condition =
		DSL.and(
			condition.id?.let { STUDY_GROUP.ID.eq(it) },
			condition.inviteCode?.let { STUDY_GROUP.INVITE_CODE.eq(it) },
		)

	fun countBy(condition: CountStudyGroupCondition): Long =
		dslContext
			.selectCount()
			.from(STUDY_GROUP)
			.where(buildCountCondition(condition).and(STUDY_GROUP.DELETED.eq(false)))
			.fetchOneInto(Long::class.java)!!

	private fun buildCountCondition(condition: CountStudyGroupCondition): Condition =
		DSL.and(
			condition.memberId?.let {
				STUDY_GROUP.ID.`in`(
					select(STUDY_GROUP_MEMBER.STUDY_GROUP_ID)
						.from(STUDY_GROUP_MEMBER)
						.where(STUDY_GROUP_MEMBER.MEMBER_ID.eq(it)),
				)
			},
		)

	fun findBy(condition: FindStudyGroupCondition): StudyGroup? {
		val fetchGroups: Map<StudyGroupRecord, Result<Record>> =
			dslContext
				.select()
				.from(STUDY_GROUP)
				.join(STUDY_GROUP_MEMBER)
				.on(STUDY_GROUP_MEMBER.STUDY_GROUP_ID.eq(STUDY_GROUP.ID))
				.where(buildDetailCondition(condition).and(STUDY_GROUP.DELETED.eq(false)))
				.fetchGroups(STUDY_GROUP)

		return studyGroupMapper.recordToStudyGroup(fetchGroups)
	}
}