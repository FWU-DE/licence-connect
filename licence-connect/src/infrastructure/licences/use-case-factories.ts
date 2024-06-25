import { AddLicenceForStudentUseCaseFactoryService } from './add-licence-for-student-use-case-factory/add-licence-for-student-use-case-factory.service';
import { ReleaseLicenceForStudentUseCaseFactoryService } from './release-licence-for-student-use-case-factory/release-licence-for-student-use-case-factory.service';
import { RemoveLicenceForStudentUseCaseFactoryService } from './remove-licence-for-student-use-case-factory/remove-licence-for-student-use-case-factory.service';
import { RequestLicenceFactoryService } from './request-licence-factory/request-licence-factory.service';

export const useCaseFactories = [
  RequestLicenceFactoryService,
  AddLicenceForStudentUseCaseFactoryService,
  ReleaseLicenceForStudentUseCaseFactoryService,
  RemoveLicenceForStudentUseCaseFactoryService,
];
