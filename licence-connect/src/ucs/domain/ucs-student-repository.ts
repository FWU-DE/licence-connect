import { UcsStudent, UcsStudentId } from './ucs-types';

export interface UcsStudentRepository {
  getUCSStudentFromId(userId: UcsStudentId): Promise<UcsStudent>;
}
