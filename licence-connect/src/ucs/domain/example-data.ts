import { AvailableLicences } from '../../licences/domain/licence';
import { RequestFromVidisCore } from '../../vidis/domain/request-from-vidis-core';
import { ResponseFromUCS } from './ucs-types';

export const incomingVidisCoreRequest: RequestFromVidisCore = {
  userId: '02e71a9d-d68d-3050-9a0d-5b963c06aec0',
  clientId: 'sodix-editor-o',
  schulkennung: 'DE-RP-SN-51201',
  bundesland: 'DE-MV',
};

export const lcLicencesFromUCSResponse: AvailableLicences = [
  {
    license_code: 'VHT-9234814-fk68-acbj6-3o9jyfilkq2pqdmxy0j',
  },
  {
    license_code: 'WES-234efsy-qer3-sdf35-ysdfpcjo3ijda09s2ds',
  },
  {
    license_code: 'HEL-72rh4f6c-f3gs-a4thg-f393tjse1kjso34w45fq',
  },
  {
    license_code: 'CCB-7bd46a45-345c-4237-a451-4444736eb011',
  },
  {
    license_code: 'CCB-2f57ko96-d32r-53wrw-3eqg-999f2242jgr45',
  },
  {
    license_code: 'COR-3rw46a45-345c-4237-a451-4333736ex015',
  },
  {
    license_code: 'EKV-g54sn7bs-d32r-5s3643eqg-999a2dok3rts',
  },
];

/**
 * Example response from the ucs system with licences
 */
export const ucsResponseWithLicences: ResponseFromUCS = {
  'http://www.bildungslogin.de/licenses': {
    id: 'sample_user_id',
    first_name: 'Sample',
    last_name: 'User',
    context: {
      oaijfds32f09sjef32rfasd: {
        school_authority: 'BBK_IN',
        school_identifier: 'HE-135109231418124',
        school_name: 'Schule A',
        classes: [
          {
            name: 'Class1',
            id: '239we423-wetiwrg23-sd0gfsd34',
            licenses: [
              'CCB-7bd46a45-345c-4237-a451-4444736eb011',
              'CCB-2f57ko96-d32r-53wrw-3eqg-999f2242jgr45',
            ],
          },
          {
            name: 'Class2',
            id: '255we323-wetiafs-2dsd0gsk20d',
          },
        ],
        workgroups: [
          {
            name: 'mathegruppe1',
            id: 'f2309jfde13jfad0fj13rjal97',
            licenses: [
              'COR-3rw46a45-345c-4237-a451-4333736ex015',
              'EKV-g54sn7bs-d32r-5s3643eqg-999a2dok3rts',
            ],
          },
          {
            name: 'mathegruppe2',
            id: 'f2309jfde13jfad0fj13rjal97',
          },
        ],
        roles: ['student'],
        licenses: ['HEL-72rh4f6c-f3gs-a4thg-f393tjse1kjso34w45fq'],
      },
    },
    licenses: [
      'VHT-9234814-fk68-acbj6-3o9jyfilkq2pqdmxy0j',
      'WES-234efsy-qer3-sdf35-ysdfpcjo3ijda09s2ds',
    ],
  },
} as ResponseFromUCS;

/**
 * Example response from the ucs system without licences
 */
export const ucsResponseWithoutLicences: ResponseFromUCS = {
  'http://www.bildungslogin.de/licenses': {
    id: 'sample_user_id',
    first_name: 'Sample',
    last_name: 'User',
    context: {
      oaijfds32f09sjef32rfasd: {
        school_authority: 'BBK_IN',
        school_identifier: 'HE-135109231418124',
        school_name: 'Schule A',
        classes: [
          {
            name: 'Class1',
            id: '239we423-wetiwrg23-sd0gfsd34',
          },
          {
            name: 'Class2',
            id: '255we323-wetiafs-2dsd0gsk20d',
          },
        ],
        workgroups: [
          {
            name: 'mathegruppe1',
            id: 'f2309jfde13jfad0fj13rjal97',
          },
          {
            name: 'mathegruppe2',
            id: 'f2309jfde13jfad0fj13rjal97',
          },
        ],
        roles: ['student'],
      },
    },
  },
};

/**
 * Example response from the ucs system with empty license Arrays
 */
export const ucsResponseWithEmptyLicences: ResponseFromUCS = {
  'http://www.bildungslogin.de/licenses': {
    id: 'sample_user_id',
    first_name: 'Sample',
    last_name: 'User',
    context: {
      oaijfds32f09sjef32rfasd: {
        school_authority: 'BBK_IN',
        school_identifier: 'HE-135109231418124',
        school_name: 'Schule A',
        classes: [
          {
            name: 'Class1',
            id: '239we423-wetiwrg23-sd0gfsd34',
            licenses: [],
          },
          {
            name: 'Class2',
            id: '255we323-wetiafs-2dsd0gsk20d',
          },
        ],
        workgroups: [
          {
            name: 'mathegruppe1',
            id: 'f2309jfde13jfad0fj13rjal97',
            licenses: [],
          },
          {
            name: 'mathegruppe2',
            id: 'f2309jfde13jfad0fj13rjal97',
          },
        ],
        roles: ['student'],
        licenses: [],
      },
    },
    licenses: [],
  },
} as ResponseFromUCS;
