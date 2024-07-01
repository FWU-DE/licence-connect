import { UCSStudent, UCSStudentId } from './ucs-types';

export interface UCSLicenceSource {
  getUCSStudentFromId(userId: UCSStudentId): UCSStudent;
}
