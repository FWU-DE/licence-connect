import { AvailableLicences } from 'domain/licence';
import { LicenceRepository } from 'domain/licence-repository';
import { StudentId } from 'domain/student';

export type RequestLicenceUseCase = (
  studentId: StudentId,
  applicationId: string,
) => AvailableLicences;

export const createSimpleLicenceRequestUseCase =
  (licenceRepository: LicenceRepository) =>
  (studentId: StudentId, _applicationId: string) => {
    const licencesForUser =
      licenceRepository.getLicencesForStudentId(studentId);

    return licencesForUser;
  };
