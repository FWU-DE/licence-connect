import { LicenceRepository } from '@licences/domain/licence-repository';
import { StudentId } from '@licences/domain/student';
import { AvailableLicences } from '@licences/domain/licence';

export type RequestLicencesForStudentUseCase = (
  studentId: StudentId,
  applicationId: string,
) => AvailableLicences;

export const createRequestLicencesForStudentUseCase =
  (licenceRepository: LicenceRepository) =>
  (studentId: StudentId, _applicationId: string) => {
    const licencesForUser =
      licenceRepository.getLicencesForStudentId(studentId);

    return licencesForUser;
  };
