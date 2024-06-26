import { StudentId } from 'domain/student';

export type ReleaseLicencesForStudentUseCase = (
  studentId: StudentId,
  applicationId: string,
) => void;

export const createReleaseLicenceForStudentUseCase: () => ReleaseLicencesForStudentUseCase =
  () => (studentId: StudentId, applicationId: string) => {
    console.log(
      `Release licence for applicaiton ${applicationId} for student ${studentId}`,
    );
  };
