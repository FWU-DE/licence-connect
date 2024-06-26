import { AvailableLicences } from 'domain/licence';
import { LicenceRepository } from 'domain/licence-repository';
import { StudentId } from 'domain/student';

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
