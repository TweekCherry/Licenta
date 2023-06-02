<template>
  <v-menu
    v-model="visible"
    :close-on-content-click="false"
    transition="slide-y-transition"
    offset-y style="max-width: 314px !important;"
  >
    <template v-slot:activator="{ on, attrs }">
      <ValidationProvider rules="required|dateFormat:dd-LL-yyyy HH:mm" :name="label" v-slot="{ errors, valid }">
      <v-text-field
        :label="label"
        :value="readOnlyDate"
        :error-messages="errors"
        :success="valid" readonly>
        <template v-slot:append>
          <v-btn icon color="primary" small v-bind="attrs" v-on="on">
            <v-icon>mdi-clock-time-nine-outline</v-icon>
          </v-btn>
        </template>
      </v-text-field>
      </ValidationProvider>
    </template>
    <v-sheet elevation="1" class="container" style="width: 314px !important;">
      <div class="row">
        <div class="col-12 pb-0">
          <ValidationProvider rules="required|dateFormat:dd-LL-yyyy HH:mm" :name="label" v-slot="{ errors, valid }" ref="validator">
            <v-text-field
              :label="label"
              prepend-icon="mdi-calendar"
              v-model="inputDateTime"
              @input="updateDateTime"
              :error-messages="errors"
              :success="valid"
              readonly
            ></v-text-field>
          </ValidationProvider>
        </div>
        <div class="col-6">
          <v-select v-model="selectedHour" :items="hours" label="Hour" dense hide-details></v-select>
        </div>
        <div class="col-6">
          <v-select v-model="selectedMinute" :items="minutes" label="Minutes" dense hide-details></v-select>
        </div>
        <div class="col-12">
          <v-date-picker :value="inputDate" :min="minDate" scrollable show-current no-title full-width color="primary" @input="updateDate"></v-date-picker>
        </div>
        <div class="col-6">
          <v-btn outlined block small color="accent" @click="resetAndHide">Cancel</v-btn>
        </div>
        <div class="col-6">
          <v-btn outlined block small color="primary" @click="applyDateTime">Select</v-btn>
        </div>
      </div>
    </v-sheet>
  </v-menu>
</template>
<script>
import { ValidationProvider } from 'vee-validate'
import { DateTime as d } from 'luxon'
const DateTime = d
export default {
  name: 'DateTimePicker',
  model: {
    prop: 'date',
    event: 'input'
  },
  components: {
    ValidationProvider
  },
  props: {
    date: {
      type: [Object, String],
      default: () => DateTime.now().setZone('utc', { keepLocalTime: true })
    },
    icon: {
      type: Boolean,
      default: false
    },
    iso: {
      type: Boolean,
      default: false
    },
    label: {
      type: String,
      default: () => 'Start date'
    },
    rules: {
      type: String,
      default: ''
    },
    min: {
      type: [Object, String],
      default: () => null
    }
  },
  computed: {
    minDate() {
      if (this.min !== null) {
        let result = this.min
        if (typeof this.min === 'string') {
          result = DateTime.fromISO(this.min, { zone: 'utc' })
        }
        return result.toISODate()
      }
      return null
    },
    selectedHour: {
      get() {
        return this.selectedDateTime.hour
      },
      set(hour) {
        this.selectedDateTime = this.selectedDateTime.set({ hour: hour })
        this.inputDateTime = this.selectedDateTime.toFormat('dd-LL-yyyy HH:mm')
      }
    },
    selectedMinute: {
      get() {
        return this.selectedDateTime.minute
      },
      set(minute) {
        this.selectedDateTime = this.selectedDateTime.set({ minute: minute })
        this.inputDateTime = this.selectedDateTime.toFormat('dd-LL-yyyy HH:mm')
      }
    },
    readOnlyDate() {
      if (typeof this.date === 'string') {
        return DateTime.fromISO(this.date, { zone: 'utc' }).toFormat('dd-LL-yyyy HH:mm')
      }
      return this.date.toFormat('dd-LL-yyyy HH:mm')
    },
    inputDate() {
      return this.selectedDateTime.toISODate()
    }
  },
  data: function() {
    return {
      visible: false,
      inputDateTime: null,
      selectedDateTime: DateTime.now(),
      hours: [8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19],
      minutes: [0, 30]
    }
  },
  methods: {
    updateDateTime(dateTime) {
      const toValidate = DateTime.fromFormat(dateTime, 'dd-LL-yyyy HH:mm', { zone: 'utc' })
      if (toValidate.invalid === null) {
        this.selectedDateTime = toValidate
        this.selectedHour = toValidate.hour
        this.selectedMinute = toValidate.minute
      }
    },
    updateDate(date) {
      this.selectedDateTime = DateTime.fromISO(date, { zone: 'utc' }).set({ hour: this.selectedHour, minute: this.selectedMinute })
      this.inputDateTime = this.selectedDateTime.toFormat('dd-LL-yyyy HH:mm')
    },
    resetAndHide() {
      this.visible = false
    },
    async applyDateTime() {
      const result = await this.$refs.validator.validate()
      if (result.valid) {
        // just apply the offset here before converting to UTC
        // const offset = this.selectedDateTime.offset
        const date = this.selectedDateTime.toISO()
        this.$emit('input', date)
        this.visible = false
      }
    }
  },
  watch: {
    visible(newValue, oldValue) {
      if (newValue) {
        if (typeof this.date === 'string') {
          this.selectedDateTime = DateTime.fromISO(this.date, { zone: 'utc' })
        } else {
          this.selectedDateTime = this.date
        }
        this.inputDateTime = this.readOnlyDate
      }
    }
  }
}
</script>
