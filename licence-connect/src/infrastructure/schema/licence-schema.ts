export const licenceSchema: any = {
  type: 'object',
  properties: {
    license_code: {
      type: 'string',
      example: 'VHT-9234814-fk68-acbj6-3o9jyfilkq2pqdmxy0j',
    },
    license_type: {
      type: 'string',
      enum: ['single'],
      example: 'single',
    },
    license_special_type: {
      type: 'string',
      enum: ['none'],
      example: 'none',
    },
    license_status: {
      type: 'object',
      properties: {
        assignment_date: {
          type: 'number',
          example: 1702036373,
        },
        provisioned_date: {
          type: 'number',
          example: 1702036373,
        },
        status_activation: {
          type: 'string',
          enum: ['ACTIVATED'],
          example: 'ACTIVATED',
        },
        status_validity: {
          type: 'string',
          enum: ['VALID'],
          example: 'VALID',
        },
        validity_start: {
          type: 'number',
          example: 1702036373,
        },
        validity_end: {
          type: 'number',
          example: 1702036373,
        },
      },
    },
  },
  required: ['license_code'],
};
