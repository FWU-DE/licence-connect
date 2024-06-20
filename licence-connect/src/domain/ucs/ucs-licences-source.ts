import { UCSStudent, UCSStudentId } from './ucs-types';

export interface IUCSLicenceSource {
  getUCSStudentFromId(userId: UCSStudentId): UCSStudent;
}
