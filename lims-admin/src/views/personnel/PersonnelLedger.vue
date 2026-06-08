<template>
  <div class="page-container">
    <div class="page-header">
      <div>
        <div class="page-title">人员台账</div>
        <div class="page-desc">管理实验室人员的基础信息、资质、授权、培训和考核记录</div>
      </div>
    </div>

    <el-row :gutter="16" class="stats-row">
      <el-col :span="6">
        <el-card class="stat-card total" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">人员总数</div>
              <div class="stat-value">{{ stats.total || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card valid" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><UserFilled /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">在职人数</div>
              <div class="stat-value">{{ stats.active || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card warning" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Clock /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">即将到期证书</div>
              <div class="stat-value">{{ stats.expiringSoon || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card expired" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <el-icon><Warning /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-label">已过期证书</div>
              <div class="stat-value">{{ stats.expired || 0 }}</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <el-card shadow="never" style="margin-top: 16px">
      <div class="toolbar">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索姓名、员工编号..."
          style="width: 260px"
          clearable
          @keyup.enter="fetchList"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select v-model="searchDept" placeholder="部门" clearable style="width: 140px">
          <el-option label="检测部" value="1" />
          <el-option label="质量部" value="2" />
          <el-option label="技术部" value="3" />
          <el-option label="行政部" value="4" />
          <el-option label="市场部" value="5" />
        </el-select>
        <el-select v-model="searchStatus" placeholder="状态" clearable style="width: 140px">
          <el-option label="在职" :value="1" />
          <el-option label="离职" :value="2" />
          <el-option label="休假" :value="3" />
          <el-option label="试用期" :value="4" />
        </el-select>
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          新增人员
        </el-button>
        <el-button @click="fetchList">
          <el-icon><Refresh /></el-icon>
          刷新
        </el-button>
      </div>

      <el-table
        :data="tableData"
        v-loading="loading"
        border
        stripe
        style="width: 100%; margin-top: 16px"
      >
        <el-table-column prop="employeeNo" label="员工编号" width="120" />
        <el-table-column prop="name" label="姓名" width="100" />
        <el-table-column label="性别" width="80">
          <template #default="{ row }">
            {{ row.gender === 1 ? '男' : '女' }}
          </template>
        </el-table-column>
        <el-table-column prop="deptName" label="部门" width="120" />
        <el-table-column prop="position" label="岗位" width="120" />
        <el-table-column prop="professionalTitle" label="职称" width="120" />
        <el-table-column prop="education" label="学历" width="100" />
        <el-table-column label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)" effect="light" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="240" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" link @click="handleView(row)">详情</el-button>
            <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
            <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.pageNum"
        v-model:page-size="pagination.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="pagination.total"
        layout="total, sizes, prev, pager, next, jumper"
        style="margin-top: 16px; justify-content: flex-end"
        @size-change="fetchList"
        @current-change="fetchList"
      />
    </el-card>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="1000px" top="5vh">
      <el-tabs v-model="activeTab" v-if="isView">
        <el-tab-pane label="基本信息" name="basic">
          <template #label>
            <el-icon><User /></el-icon>
            <span style="margin-left: 4px">基本信息</span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="学历经历" name="education">
          <template #label>
            <el-icon><School /></el-icon>
            <span style="margin-left: 4px">学历经历</span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="职称" name="title">
          <template #label>
            <el-icon><Medal /></el-icon>
            <span style="margin-left: 4px">职称</span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="授权项目" name="authorization">
          <template #label>
            <el-icon><Collection /></el-icon>
            <span style="margin-left: 4px">授权项目</span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="培训记录" name="training">
          <template #label>
            <el-icon><Reading /></el-icon>
            <span style="margin-left: 4px">培训记录</span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="考核记录" name="assessment">
          <template #label>
            <el-icon><Notebook /></el-icon>
            <span style="margin-left: 4px">考核记录</span>
          </template>
        </el-tab-pane>
        <el-tab-pane label="上岗证" name="certificate">
          <template #label>
            <el-icon><Certificate /></el-icon>
            <span style="margin-left: 4px">上岗证</span>
          </template>
        </el-tab-pane>
      </el-tabs>

      <div v-if="activeTab === 'basic' || !isView">
        <el-form
          :model="personnelForm"
          :rules="formRules"
          ref="personnelFormRef"
          label-width="100px"
          v-loading="detailLoading"
        >
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="员工编号" prop="employeeNo">
                <el-input v-model="personnelForm.employeeNo" placeholder="请输入员工编号" :disabled="isView" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="姓名" prop="name">
                <el-input v-model="personnelForm.name" placeholder="请输入姓名" :disabled="isView" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="16">
            <el-col :span="8">
              <el-form-item label="性别" prop="gender">
                <el-radio-group v-model="personnelForm.gender" :disabled="isView">
                  <el-radio :value="1">男</el-radio>
                  <el-radio :value="2">女</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="出生日期" prop="birthDate">
                <el-date-picker
                  v-model="personnelForm.birthDate"
                  type="date"
                  value-format="YYYY-MM-DD"
                  placeholder="选择日期"
                  style="width: 100%"
                  :disabled="isView"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="身份证号" prop="idCard">
                <el-input v-model="personnelForm.idCard" placeholder="请输入身份证号" :disabled="isView" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="16">
            <el-col :span="8">
              <el-form-item label="部门" prop="deptId">
                <el-select v-model="personnelForm.deptId" placeholder="请选择部门" :disabled="isView" style="width: 100%">
                  <el-option label="检测部" :value="1" />
                  <el-option label="质量部" :value="2" />
                  <el-option label="技术部" :value="3" />
                  <el-option label="行政部" :value="4" />
                  <el-option label="市场部" :value="5" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="岗位" prop="position">
                <el-input v-model="personnelForm.position" placeholder="请输入岗位" :disabled="isView" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="学历" prop="education">
                <el-select v-model="personnelForm.education" placeholder="请选择学历" :disabled="isView" style="width: 100%">
                  <el-option label="博士" value="博士" />
                  <el-option label="硕士" value="硕士" />
                  <el-option label="本科" value="本科" />
                  <el-option label="大专" value="大专" />
                  <el-option label="中专" value="中专" />
                  <el-option label="高中" value="高中" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="16">
            <el-col :span="8">
              <el-form-item label="职称" prop="professionalTitle">
                <el-select v-model="personnelForm.professionalTitle" placeholder="请选择职称" :disabled="isView" style="width: 100%">
                  <el-option label="正高级" value="正高级" />
                  <el-option label="副高级" value="副高级" />
                  <el-option label="中级" value="中级" />
                  <el-option label="初级" value="初级" />
                  <el-option label="助理" value="助理" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="入职日期" prop="hireDate">
                <el-date-picker
                  v-model="personnelForm.hireDate"
                  type="date"
                  value-format="YYYY-MM-DD"
                  placeholder="选择日期"
                  style="width: 100%"
                  :disabled="isView"
                />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="状态" prop="status">
                <el-select v-model="personnelForm.status" placeholder="请选择状态" :disabled="isView" style="width: 100%">
                  <el-option label="在职" :value="1" />
                  <el-option label="离职" :value="2" />
                  <el-option label="休假" :value="3" />
                  <el-option label="试用期" :value="4" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>

          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="联系电话" prop="phone">
                <el-input v-model="personnelForm.phone" placeholder="请输入联系电话" :disabled="isView" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="电子邮箱" prop="email">
                <el-input v-model="personnelForm.email" placeholder="请输入电子邮箱" :disabled="isView" />
              </el-form-item>
            </el-col>
          </el-row>

          <el-form-item label="家庭住址" prop="address">
            <el-input v-model="personnelForm.address" placeholder="请输入家庭住址" :disabled="isView" />
          </el-form-item>

          <el-form-item label="备注" prop="remark">
            <el-input
              v-model="personnelForm.remark"
              type="textarea"
              :rows="2"
              placeholder="请输入备注"
              :disabled="isView"
            />
          </el-form-item>
        </el-form>
      </div>

      <div v-if="isView && activeTab === 'education'">
        <div class="sub-toolbar">
          <el-button type="primary" size="small" @click="handleAddEducation">
            <el-icon><Plus /></el-icon>
            新增
          </el-button>
        </div>
        <el-table :data="detailData.educationList || []" border stripe style="width: 100%; margin-top: 12px">
          <el-table-column prop="graduationSchool" label="毕业院校" width="180" />
          <el-table-column prop="major" label="专业" width="160" />
          <el-table-column prop="education" label="学历" width="100" />
          <el-table-column prop="degree" label="学位" width="100" />
          <el-table-column prop="startDate" label="开始日期" width="120" />
          <el-table-column prop="endDate" label="结束日期" width="120" />
          <el-table-column prop="certificateNo" label="证书编号" width="160" />
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="handleEditEducation(row)">编辑</el-button>
              <el-button type="danger" link size="small" @click="handleDeleteEducation(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div v-if="isView && activeTab === 'title'">
        <div class="sub-toolbar">
          <el-button type="primary" size="small" @click="handleAddTitle">
            <el-icon><Plus /></el-icon>
            新增
          </el-button>
        </div>
        <el-table :data="detailData.titleList || []" border stripe style="width: 100%; margin-top: 12px">
          <el-table-column prop="titleName" label="职称名称" width="160" />
          <el-table-column prop="titleLevel" label="职称级别" width="120" />
          <el-table-column prop="certificateNo" label="证书编号" width="160" />
          <el-table-column prop="acquireDate" label="取得日期" width="120" />
          <el-table-column prop="grantingAuthority" label="授予机构" width="180" />
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="handleEditTitle(row)">编辑</el-button>
              <el-button type="danger" link size="small" @click="handleDeleteTitle(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div v-if="isView && activeTab === 'authorization'">
        <div class="sub-toolbar">
          <el-button type="primary" size="small" @click="handleAddAuthorization">
            <el-icon><Plus /></el-icon>
            新增
          </el-button>
        </div>
        <el-table :data="detailData.authorizationList || []" border stripe style="width: 100%; margin-top: 12px">
          <el-table-column prop="authorizationType" label="授权类型" width="140" />
          <el-table-column prop="itemName" label="检测项目" width="160" />
          <el-table-column prop="standardName" label="检测标准" min-width="200" show-overflow-tooltip />
          <el-table-column prop="methodName" label="检测方法" min-width="160" show-overflow-tooltip />
          <el-table-column prop="authorizeDate" label="授权日期" width="120" />
          <el-table-column prop="expiryDate" label="有效期至" width="120" />
          <el-table-column label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="row.status === 1 ? 'success' : 'info'" effect="light" size="small">
                {{ row.status === 1 ? '已启用' : '已停用' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="handleEditAuthorization(row)">编辑</el-button>
              <el-button :type="row.status === 1 ? 'warning' : 'success'" link size="small" @click="handleToggleAuthorization(row)">
                {{ row.status === 1 ? '停用' : '启用' }}
              </el-button>
              <el-button type="danger" link size="small" @click="handleDeleteAuthorization(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div v-if="isView && activeTab === 'training'">
        <div class="sub-toolbar">
          <el-button type="primary" size="small" @click="handleAddTraining">
            <el-icon><Plus /></el-icon>
            新增
          </el-button>
        </div>
        <el-table :data="detailData.trainingRecordList || []" border stripe style="width: 100%; margin-top: 12px">
          <el-table-column prop="trainingName" label="培训名称" width="180" />
          <el-table-column prop="trainingType" label="培训类型" width="120" />
          <el-table-column prop="trainingContent" label="培训内容" min-width="200" show-overflow-tooltip />
          <el-table-column prop="trainingDate" label="培训日期" width="120" />
          <el-table-column prop="trainingHours" label="培训时长(小时)" width="120" />
          <el-table-column prop="trainingOrganization" label="培训机构" width="160" />
          <el-table-column label="结果" width="100">
            <template #default="{ row }">
              <el-tag :type="row.result === '合格' ? 'success' : 'danger'" effect="light" size="small">
                {{ row.result }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="handleEditTraining(row)">编辑</el-button>
              <el-button type="danger" link size="small" @click="handleDeleteTraining(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div v-if="isView && activeTab === 'assessment'">
        <div class="sub-toolbar">
          <el-button type="primary" size="small" @click="handleAddAssessment">
            <el-icon><Plus /></el-icon>
            新增
          </el-button>
        </div>
        <el-table :data="detailData.assessmentList || []" border stripe style="width: 100%; margin-top: 12px">
          <el-table-column prop="assessmentName" label="考核名称" width="180" />
          <el-table-column prop="assessmentType" label="考核类型" width="120" />
          <el-table-column prop="assessmentContent" label="考核内容" min-width="200" show-overflow-tooltip />
          <el-table-column prop="assessmentDate" label="考核日期" width="120" />
          <el-table-column prop="assessmentOrg" label="考核机构" width="160" />
          <el-table-column prop="score" label="分数" width="100" />
          <el-table-column label="结果" width="100">
            <template #default="{ row }">
              <el-tag :type="row.result === '合格' ? 'success' : 'danger'" effect="light" size="small">
                {{ row.result }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="handleEditAssessment(row)">编辑</el-button>
              <el-button type="danger" link size="small" @click="handleDeleteAssessment(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div v-if="isView && activeTab === 'certificate'">
        <div class="sub-toolbar">
          <el-button type="primary" size="small" @click="handleAddCertificate">
            <el-icon><Plus /></el-icon>
            新增
          </el-button>
        </div>
        <el-table :data="detailData.certificateList || []" border stripe style="width: 100%; margin-top: 12px">
          <el-table-column prop="certificateName" label="证书名称" width="180" />
          <el-table-column prop="certificateNo" label="证书编号" width="180" />
          <el-table-column prop="issuingAuthority" label="发证机构" width="160" />
          <el-table-column prop="issueDate" label="发证日期" width="120" />
          <el-table-column prop="expiryDate" label="有效期至" width="120" />
          <el-table-column label="状态" width="120">
            <template #default="{ row }">
              <el-tag v-if="isCertificateExpired(row)" type="danger" effect="light" size="small">已过期</el-tag>
              <el-tag v-else-if="isCertificateExpiringSoon(row)" type="warning" effect="light" size="small">即将到期</el-tag>
              <el-tag v-else type="success" effect="light" size="small">有效</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="剩余天数" width="100">
            <template #default="{ row }">
              <span :style="{ color: getCertificateDaysColor(row) }">
                {{ getCertificateDays(row) }}天
              </span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" link size="small" @click="handleEditCertificate(row)">编辑</el-button>
              <el-button type="danger" link size="small" @click="handleDeleteCertificate(row)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <template #footer>
        <el-button @click="dialogVisible = false">关闭</el-button>
        <el-button v-if="!isView" type="primary" @click="handleSubmit">
          <el-icon><Check /></el-icon>
          确定
        </el-button>
      </template>
    </el-dialog>

    <el-dialog v-model="subDialogVisible" :title="subDialogTitle" width="600px" top="10vh">
      <el-form
        :model="subForm"
        :rules="subFormRules"
        ref="subFormRef"
        label-width="100px"
      >
        <template v-if="activeSubTab === 'education'">
          <el-form-item label="毕业院校" prop="graduationSchool">
            <el-input v-model="subForm.graduationSchool" placeholder="请输入毕业院校" />
          </el-form-item>
          <el-form-item label="专业" prop="major">
            <el-input v-model="subForm.major" placeholder="请输入专业" />
          </el-form-item>
          <el-form-item label="学历" prop="education">
            <el-select v-model="subForm.education" placeholder="请选择学历" style="width: 100%">
              <el-option label="博士" value="博士" />
              <el-option label="硕士" value="硕士" />
              <el-option label="本科" value="本科" />
              <el-option label="大专" value="大专" />
              <el-option label="中专" value="中专" />
              <el-option label="高中" value="高中" />
            </el-select>
          </el-form-item>
          <el-form-item label="学位" prop="degree">
            <el-select v-model="subForm.degree" placeholder="请选择学位" style="width: 100%">
              <el-option label="博士" value="博士" />
              <el-option label="硕士" value="硕士" />
              <el-option label="学士" value="学士" />
              <el-option label="无" value="无" />
            </el-select>
          </el-form-item>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="开始日期" prop="startDate">
                <el-date-picker
                  v-model="subForm.startDate"
                  type="date"
                  value-format="YYYY-MM-DD"
                  placeholder="选择日期"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="结束日期" prop="endDate">
                <el-date-picker
                  v-model="subForm.endDate"
                  type="date"
                  value-format="YYYY-MM-DD"
                  placeholder="选择日期"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="证书编号" prop="certificateNo">
            <el-input v-model="subForm.certificateNo" placeholder="请输入证书编号" />
          </el-form-item>
        </template>

        <template v-if="activeSubTab === 'title'">
          <el-form-item label="职称名称" prop="titleName">
            <el-input v-model="subForm.titleName" placeholder="请输入职称名称" />
          </el-form-item>
          <el-form-item label="职称级别" prop="titleLevel">
            <el-select v-model="subForm.titleLevel" placeholder="请选择职称级别" style="width: 100%">
              <el-option label="正高级" value="正高级" />
              <el-option label="副高级" value="副高级" />
              <el-option label="中级" value="中级" />
              <el-option label="初级" value="初级" />
              <el-option label="助理" value="助理" />
            </el-select>
          </el-form-item>
          <el-form-item label="证书编号" prop="certificateNo">
            <el-input v-model="subForm.certificateNo" placeholder="请输入证书编号" />
          </el-form-item>
          <el-form-item label="取得日期" prop="acquireDate">
            <el-date-picker
              v-model="subForm.acquireDate"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="选择日期"
              style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="授予机构" prop="grantingAuthority">
            <el-input v-model="subForm.grantingAuthority" placeholder="请输入授予机构" />
          </el-form-item>
        </template>

        <template v-if="activeSubTab === 'authorization'">
          <el-form-item label="授权类型" prop="authorizationType">
            <el-select v-model="subForm.authorizationType" placeholder="请选择授权类型" style="width: 100%">
              <el-option label="检测参数授权" value="检测参数授权" />
              <el-option label="检测方法授权" value="检测方法授权" />
              <el-option label="仪器操作授权" value="仪器操作授权" />
              <el-option label="报告签发授权" value="报告签发授权" />
            </el-select>
          </el-form-item>
          <el-form-item label="检测项目" prop="itemId">
            <el-select
              v-model="subForm.itemId"
              placeholder="请选择检测项目"
              style="width: 100%"
              filterable
              @change="handleTestItemChange"
            >
              <el-option
                v-for="item in testItemOptions"
                :key="item.id"
                :label="item.itemName"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="检测标准" prop="standardId">
            <el-select
              v-model="subForm.standardId"
              placeholder="请选择检测标准"
              style="width: 100%"
              filterable
              @change="handleStandardChange"
            >
              <el-option
                v-for="item in standardOptions"
                :key="item.id"
                :label="item.standardNo + ' ' + item.standardName"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="检测方法" prop="methodName">
            <el-input v-model="subForm.methodName" placeholder="请输入检测方法" />
          </el-form-item>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="授权日期" prop="authorizeDate">
                <el-date-picker
                  v-model="subForm.authorizeDate"
                  type="date"
                  value-format="YYYY-MM-DD"
                  placeholder="选择日期"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="有效期至" prop="expiryDate">
                <el-date-picker
                  v-model="subForm.expiryDate"
                  type="date"
                  value-format="YYYY-MM-DD"
                  placeholder="选择日期"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>
        </template>

        <template v-if="activeSubTab === 'training'">
          <el-form-item label="培训名称" prop="trainingName">
            <el-input v-model="subForm.trainingName" placeholder="请输入培训名称" />
          </el-form-item>
          <el-form-item label="培训类型" prop="trainingType">
            <el-select v-model="subForm.trainingType" placeholder="请选择培训类型" style="width: 100%">
              <el-option label="岗前培训" value="岗前培训" />
              <el-option label="在岗培训" value="在岗培训" />
              <el-option label="技能培训" value="技能培训" />
              <el-option label="安全培训" value="安全培训" />
              <el-option label="管理培训" value="管理培训" />
            </el-select>
          </el-form-item>
          <el-form-item label="培训内容" prop="trainingContent">
            <el-input v-model="subForm.trainingContent" type="textarea" :rows="2" placeholder="请输入培训内容" />
          </el-form-item>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="培训日期" prop="trainingDate">
                <el-date-picker
                  v-model="subForm.trainingDate"
                  type="date"
                  value-format="YYYY-MM-DD"
                  placeholder="选择日期"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="培训时长(小时)" prop="trainingHours">
                <el-input-number v-model="subForm.trainingHours" :min="0" style="width: 100%" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="结果" prop="result">
                <el-select v-model="subForm.result" placeholder="请选择结果" style="width: 100%">
                  <el-option label="合格" value="合格" />
                  <el-option label="不合格" value="不合格" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="是否获得证书" prop="certificateFlag">
                <el-select v-model="subForm.certificateFlag" placeholder="请选择" style="width: 100%">
                  <el-option label="否" :value="0" />
                  <el-option label="是" :value="1" />
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="培训机构" prop="trainingOrganization">
            <el-input v-model="subForm.trainingOrganization" placeholder="请输入培训机构" />
          </el-form-item>
          <el-form-item label="证书编号" prop="certificateNo">
            <el-input v-model="subForm.certificateNo" placeholder="请输入证书编号" />
          </el-form-item>
          <el-form-item label="证书到期日期" prop="certificateExpiryDate">
            <el-date-picker
              v-model="subForm.certificateExpiryDate"
              type="date"
              value-format="YYYY-MM-DD"
              placeholder="选择日期"
              style="width: 100%"
            />
          </el-form-item>
        </template>

        <template v-if="activeSubTab === 'assessment'">
          <el-form-item label="考核名称" prop="assessmentName">
            <el-input v-model="subForm.assessmentName" placeholder="请输入考核名称" />
          </el-form-item>
          <el-form-item label="考核类型" prop="assessmentType">
            <el-select v-model="subForm.assessmentType" placeholder="请选择考核类型" style="width: 100%">
              <el-option label="理论考核" value="理论考核" />
              <el-option label="实操考核" value="实操考核" />
              <el-option label="年度考核" value="年度考核" />
              <el-option label="试用期考核" value="试用期考核" />
            </el-select>
          </el-form-item>
          <el-form-item label="考核内容" prop="assessmentContent">
            <el-input v-model="subForm.assessmentContent" type="textarea" :rows="2" placeholder="请输入考核内容" />
          </el-form-item>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="考核日期" prop="assessmentDate">
                <el-date-picker
                  v-model="subForm.assessmentDate"
                  type="date"
                  value-format="YYYY-MM-DD"
                  placeholder="选择日期"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="分数" prop="score">
                <el-input-number v-model="subForm.score" :min="0" :max="100" style="width: 100%" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="结果" prop="result">
                <el-select v-model="subForm.result" placeholder="请选择结果" style="width: 100%">
                  <el-option label="合格" value="合格" />
                  <el-option label="不合格" value="不合格" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="考核机构" prop="assessmentOrg">
                <el-input v-model="subForm.assessmentOrg" placeholder="请输入考核机构" />
              </el-form-item>
            </el-col>
          </el-row>
        </template>

        <template v-if="activeSubTab === 'certificate'">
          <el-form-item label="证书类型" prop="certificateType">
            <el-select v-model="subForm.certificateType" placeholder="请选择证书类型" style="width: 100%">
              <el-option label="上岗证" value="上岗证" />
              <el-option label="资格证" value="资格证" />
              <el-option label="操作证" value="操作证" />
              <el-option label="培训证" value="培训证" />
              <el-option label="其他" value="其他" />
            </el-select>
          </el-form-item>
          <el-form-item label="证书名称" prop="certificateName">
            <el-input v-model="subForm.certificateName" placeholder="请输入证书名称" />
          </el-form-item>
          <el-form-item label="证书编号" prop="certificateNo">
            <el-input v-model="subForm.certificateNo" placeholder="请输入证书编号" />
          </el-form-item>
          <el-form-item label="发证机构" prop="issuingAuthority">
            <el-input v-model="subForm.issuingAuthority" placeholder="请输入发证机构" />
          </el-form-item>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="发证日期" prop="issueDate">
                <el-date-picker
                  v-model="subForm.issueDate"
                  type="date"
                  value-format="YYYY-MM-DD"
                  placeholder="选择日期"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="有效期至" prop="expiryDate">
                <el-date-picker
                  v-model="subForm.expiryDate"
                  type="date"
                  value-format="YYYY-MM-DD"
                  placeholder="选择日期"
                  style="width: 100%"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="16">
            <el-col :span="12">
              <el-form-item label="是否提醒" prop="isRemind">
                <el-select v-model="subForm.isRemind" placeholder="请选择" style="width: 100%">
                  <el-option label="否" :value="0" />
                  <el-option label="是" :value="1" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="提醒天数" prop="remindDays">
                <el-input-number v-model="subForm.remindDays" :min="1" :max="365" style="width: 100%" />
              </el-form-item>
            </el-col>
          </el-row>
        </template>
      </el-form>

      <template #footer>
        <el-button @click="subDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubSubmit">
          <el-icon><Check /></el-icon>
          确定
        </el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  User,
  UserFilled,
  School,
  Medal,
  Collection,
  Reading,
  Notebook,
  Certificate,
  Warning,
  Clock,
  Check,
  Plus,
  Refresh,
  Search
} from '@element-plus/icons-vue'
import { personnelApi } from '@/api/personnel'

const loading = ref(false)
const detailLoading = ref(false)
const dialogVisible = ref(false)
const subDialogVisible = ref(false)
const isView = ref(false)
const dialogTitle = ref('')
const subDialogTitle = ref('')
const activeTab = ref('basic')
const activeSubTab = ref('')

const searchKeyword = ref('')
const searchDept = ref('')
const searchStatus = ref('')

const stats = reactive({
  total: 0,
  active: 0,
  expiringSoon: 0,
  expired: 0
})

const tableData = ref([])
const detailData = ref({})

const pagination = reactive({
  pageNum: 1,
  pageSize: 10,
  total: 0
})

const personnelForm = ref({
  id: null,
  employeeNo: '',
  name: '',
  gender: 1,
  birthDate: '',
  idCard: '',
  deptId: null,
  deptName: '',
  position: '',
  education: '',
  professionalTitle: '',
  hireDate: '',
  status: 1,
  phone: '',
  email: '',
  address: '',
  remark: ''
})

const formRules = {
  employeeNo: [{ required: true, message: '请输入员工编号', trigger: 'blur' }],
  name: [{ required: true, message: '请输入姓名', trigger: 'blur' }]
}

const personnelFormRef = ref(null)
const subFormRef = ref(null)

const subForm = ref({})

const testItemOptions = ref([
  { id: 1, itemCode: 'W001', itemName: 'pH值', itemType: '水质检测' },
  { id: 2, itemCode: 'W002', itemName: '化学需氧量(COD)', itemType: '水质检测' },
  { id: 3, itemCode: 'W003', itemName: '生化需氧量(BOD5)', itemType: '水质检测' },
  { id: 4, itemCode: 'W004', itemName: '氨氮', itemType: '水质检测' },
  { id: 5, itemCode: 'W005', itemName: '总磷', itemType: '水质检测' },
  { id: 6, itemCode: 'W006', itemName: '总氮', itemType: '水质检测' },
  { id: 7, itemCode: 'A001', itemName: '二氧化硫', itemType: '大气检测' },
  { id: 8, itemCode: 'A002', itemName: '氮氧化物', itemType: '大气检测' },
  { id: 9, itemCode: 'A003', itemName: '颗粒物(PM10)', itemType: '大气检测' },
  { id: 10, itemCode: 'A004', itemName: '颗粒物(PM2.5)', itemType: '大气检测' },
  { id: 11, itemCode: 'S001', itemName: '铜', itemType: '土壤检测' },
  { id: 12, itemCode: 'S002', itemName: '铅', itemType: '土壤检测' },
  { id: 13, itemCode: 'S003', itemName: '镉', itemType: '土壤检测' },
  { id: 14, itemCode: 'N001', itemName: '等效连续A声级', itemType: '噪声检测' },
  { id: 15, itemCode: 'N002', itemName: '昼间等效声级', itemType: '噪声检测' }
])

const standardOptions = ref([
  { id: 1, standardNo: 'GB 3838-2002', standardName: '地表水环境质量标准' },
  { id: 2, standardNo: 'GB 8978-1996', standardName: '污水综合排放标准' },
  { id: 3, standardNo: 'GB 3095-2012', standardName: '环境空气质量标准' },
  { id: 4, standardNo: 'GB 16297-1996', standardName: '大气污染物综合排放标准' },
  { id: 5, standardNo: 'GB 15618-2018', standardName: '土壤环境质量 农用地土壤污染风险管控标准' },
  { id: 6, standardNo: 'GB 36600-2018', standardName: '土壤环境质量 建设用地土壤污染风险管控标准' },
  { id: 7, standardNo: 'GB 3096-2008', standardName: '声环境质量标准' },
  { id: 8, standardNo: 'GB 12348-2008', standardName: '工业企业厂界环境噪声排放标准' },
  { id: 9, standardNo: 'GB 11607-89', standardName: '渔业水质标准' },
  { id: 10, standardNo: 'GB 5749-2006', standardName: '生活饮用水卫生标准' }
])

const handleTestItemChange = (itemId) => {
  const selectedItem = testItemOptions.value.find(item => item.id === itemId)
  if (selectedItem) {
    subForm.value.itemName = selectedItem.itemName
    subForm.value.itemCode = selectedItem.itemCode
  }
}

const handleStandardChange = (standardId) => {
  const selectedStandard = standardOptions.value.find(item => item.id === standardId)
  if (selectedStandard) {
    subForm.value.standardName = selectedStandard.standardName
    subForm.value.standardNo = selectedStandard.standardNo
  }
}
const subFormRules = {
  graduationSchool: [{ required: true, message: '请输入毕业院校', trigger: 'blur' }],
  major: [{ required: true, message: '请输入专业', trigger: 'blur' }],
  education: [{ required: true, message: '请选择学历', trigger: 'change' }],
  titleName: [{ required: true, message: '请输入职称名称', trigger: 'blur' }],
  titleLevel: [{ required: true, message: '请选择职称级别', trigger: 'change' }],
  authorizationType: [{ required: true, message: '请选择授权类型', trigger: 'change' }],
  itemId: [{ required: true, message: '请选择检测项目', trigger: 'change' }],
  standardId: [{ required: true, message: '请选择检测标准', trigger: 'change' }],
  methodName: [{ required: true, message: '请输入检测方法', trigger: 'blur' }],
  trainingName: [{ required: true, message: '请输入培训名称', trigger: 'blur' }],
  trainingType: [{ required: true, message: '请选择培训类型', trigger: 'change' }],
  assessmentName: [{ required: true, message: '请输入考核名称', trigger: 'blur' }],
  assessmentType: [{ required: true, message: '请选择考核类型', trigger: 'change' }],
  certificateType: [{ required: true, message: '请选择证书类型', trigger: 'change' }],
  certificateName: [{ required: true, message: '请输入证书名称', trigger: 'blur' }],
  certificateNo: [{ required: true, message: '请输入证书编号', trigger: 'blur' }]
}

const getStatusTag = (status) => {
  const tags = { 1: 'success', 2: 'info', 3: 'warning', 4: 'primary' }
  return tags[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { 1: '在职', 2: '离职', 3: '休假', 4: '试用期' }
  return texts[status] || '未知'
}

const isCertificateExpired = (row) => {
  if (!row.expiryDate) return false
  const now = new Date()
  const endDate = new Date(row.expiryDate)
  return endDate < now
}

const isCertificateExpiringSoon = (row) => {
  if (!row.expiryDate) return false
  const now = new Date()
  const endDate = new Date(row.expiryDate)
  const diffDays = Math.ceil((endDate - now) / (1000 * 60 * 60 * 24))
  return diffDays >= 0 && diffDays <= 30
}

const getCertificateDays = (row) => {
  if (!row.expiryDate) return 0
  const now = new Date()
  const endDate = new Date(row.expiryDate)
  const diffDays = Math.ceil((endDate - now) / (1000 * 60 * 60 * 24))
  return diffDays
}

const getCertificateDaysColor = (row) => {
  if (isCertificateExpired(row)) return '#f56c6c'
  if (isCertificateExpiringSoon(row)) return '#e6a23c'
  return '#67c23a'
}

const fetchStats = async () => {
  try {
    const res = await personnelApi.stats()
    if (res.code === 200) {
      Object.assign(stats, res.data)
    }
  } catch (e) {
    console.error(e)
  }
}

const fetchList = async () => {
  loading.value = true
  try {
    const params = {
      pageNum: pagination.pageNum,
      pageSize: pagination.pageSize,
      name: searchKeyword.value,
      employeeNo: searchKeyword.value,
      deptId: searchDept.value,
      status: searchStatus.value
    }
    const res = await personnelApi.page(params)
    if (res.code === 200) {
      tableData.value = res.data.list
      pagination.total = res.data.total
    }
  } finally {
    loading.value = false
  }
}

const handleAdd = () => {
  isView.value = false
  dialogTitle.value = '新增人员'
  personnelForm.value = {
    id: null,
    employeeNo: '',
    name: '',
    gender: 1,
    birthDate: '',
    idCard: '',
    deptId: null,
    deptName: '',
    position: '',
    education: '',
    professionalTitle: '',
    hireDate: '',
    status: 1,
    phone: '',
    email: '',
    address: '',
    remark: ''
  }
  dialogVisible.value = true
}

const fetchAllSubLists = async (personnelId) => {
  if (!personnelId) return
  await Promise.all([
    fetchSubList('education'),
    fetchSubList('title'),
    fetchSubList('authorization'),
    fetchSubList('training'),
    fetchSubList('assessment'),
    fetchSubList('certificate')
  ])
}

const handleEdit = async (row) => {
  isView.value = false
  dialogTitle.value = '编辑人员'
  personnelForm.value = { ...row }
  detailLoading.value = true
  try {
    const res = await personnelApi.get(row.id)
    if (res.code === 200) {
      personnelForm.value = { ...res.data }
      detailData.value = res.data
    }
  } finally {
    detailLoading.value = false
  }
  dialogVisible.value = true
}

const handleView = async (row) => {
  isView.value = true
  dialogTitle.value = '人员详情'
  activeTab.value = 'basic'
  detailLoading.value = true
  try {
    const res = await personnelApi.get(row.id)
    if (res.code === 200) {
      personnelForm.value = { ...res.data }
      detailData.value = res.data
    }
  } finally {
    detailLoading.value = false
  }
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除人员"${row.name}"吗？`, '提示', {
    type: 'warning'
  }).then(async () => {
    const res = await personnelApi.delete(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      fetchList()
      fetchStats()
    }
  }).catch(() => {})
}

const handleSubmit = async () => {
  if (!personnelFormRef.value) return
  try {
    await personnelFormRef.value.validate()
    let res
    if (personnelForm.value.id) {
      res = await personnelApi.update(personnelForm.value)
    } else {
      res = await personnelApi.save(personnelForm.value)
    }
    if (res.code === 200) {
      ElMessage.success(personnelForm.value.id ? '更新成功' : '新增成功')
      dialogVisible.value = false
      fetchList()
      fetchStats()
    }
  } catch (e) {
    console.error(e)
  }
}

const initSubForm = () => {
  const subFormMap = {
    education: {
      id: null,
      personnelId: null,
      graduationSchool: '',
      major: '',
      education: '',
      degree: '',
      startDate: '',
      endDate: '',
      certificateNo: '',
      isFullTime: 1,
      remark: ''
    },
    title: {
      id: null,
      personnelId: null,
      titleName: '',
      titleLevel: '',
      certificateNo: '',
      acquireDate: '',
      grantingAuthority: '',
      remark: ''
    },
    authorization: {
      id: null,
      personnelId: null,
      authorizationType: '',
      itemId: null,
      itemName: '',
      itemCode: '',
      standardId: null,
      standardName: '',
      standardNo: '',
      methodName: '',
      authorizeDate: '',
      expiryDate: '',
      status: 1,
      remark: ''
    },
    training: {
      id: null,
      personnelId: null,
      personnelName: '',
      trainingName: '',
      trainingType: '',
      trainingContent: '',
      trainingDate: '',
      trainingHours: null,
      trainingOrganization: '',
      certificateFlag: 0,
      certificateNo: '',
      certificateExpiryDate: '',
      result: '',
      remark: ''
    },
    assessment: {
      id: null,
      personnelId: null,
      assessmentName: '',
      assessmentType: '',
      assessmentContent: '',
      assessmentDate: '',
      assessmentOrg: '',
      score: null,
      result: '',
      remark: ''
    },
    certificate: {
      id: null,
      personnelId: null,
      certificateType: '',
      certificateName: '',
      certificateNo: '',
      issuingAuthority: '',
      issueDate: '',
      expiryDate: '',
      isRemind: 1,
      remindDays: 30,
      status: 1,
      remark: ''
    }
  }
  return subFormMap[activeSubTab.value] || {}
}

const getSubDialogTitle = (action) => {
  const titleMap = {
    education: action === 'add' ? '新增学历经历' : '编辑学历经历',
    title: action === 'add' ? '新增职称' : '编辑职称',
    authorization: action === 'add' ? '新增授权项目' : '编辑授权项目',
    training: action === 'add' ? '新增培训记录' : '编辑培训记录',
    assessment: action === 'add' ? '新增考核记录' : '编辑考核记录',
    certificate: action === 'add' ? '新增上岗证' : '编辑上岗证'
  }
  return titleMap[activeSubTab.value] || ''
}

const handleAddEducation = () => {
  activeSubTab.value = 'education'
  subDialogTitle.value = getSubDialogTitle('add')
  subForm.value = initSubForm()
  subForm.value.personnelId = personnelForm.value.id
  subDialogVisible.value = true
}

const handleEditEducation = (row) => {
  activeSubTab.value = 'education'
  subDialogTitle.value = getSubDialogTitle('edit')
  subForm.value = { ...row }
  subDialogVisible.value = true
}

const handleDeleteEducation = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该学历经历吗？', '提示', { type: 'warning' })
    const res = await personnelApi.educationDelete(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      await fetchSubList('education')
    }
  } catch (e) {
    if (e !== 'cancel') {
      console.error(e)
    }
  }
}

const handleAddTitle = () => {
  activeSubTab.value = 'title'
  subDialogTitle.value = getSubDialogTitle('add')
  subForm.value = initSubForm()
  subForm.value.personnelId = personnelForm.value.id
  subDialogVisible.value = true
}

const handleEditTitle = (row) => {
  activeSubTab.value = 'title'
  subDialogTitle.value = getSubDialogTitle('edit')
  subForm.value = { ...row }
  subDialogVisible.value = true
}

const handleDeleteTitle = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该职称吗？', '提示', { type: 'warning' })
    const res = await personnelApi.titleDelete(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      await fetchSubList('title')
    }
  } catch (e) {
    if (e !== 'cancel') {
      console.error(e)
    }
  }
}

const handleAddAuthorization = () => {
  activeSubTab.value = 'authorization'
  subDialogTitle.value = getSubDialogTitle('add')
  subForm.value = initSubForm()
  subForm.value.personnelId = personnelForm.value.id
  subDialogVisible.value = true
}

const handleEditAuthorization = (row) => {
  activeSubTab.value = 'authorization'
  subDialogTitle.value = getSubDialogTitle('edit')
  subForm.value = { ...row }
  subDialogVisible.value = true
}

const handleToggleAuthorization = async (row) => {
  try {
    const newStatus = row.status === 1 ? 0 : 1
    const apiMethod = newStatus === 1 ? 'authorizationEnable' : 'authorizationDisable'
    const res = await personnelApi[apiMethod](row.id)
    if (res.code === 200) {
      row.status = newStatus
      ElMessage.success(newStatus === 1 ? '已启用' : '已停用')
    }
  } catch (e) {
    console.error(e)
  }
}

const handleDeleteAuthorization = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该授权项目吗？', '提示', { type: 'warning' })
    const res = await personnelApi.authorizationDelete(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      await fetchSubList('authorization')
    }
  } catch (e) {
    if (e !== 'cancel') {
      console.error(e)
    }
  }
}

const handleAddTraining = () => {
  activeSubTab.value = 'training'
  subDialogTitle.value = getSubDialogTitle('add')
  subForm.value = initSubForm()
  subForm.value.personnelId = personnelForm.value.id
  subDialogVisible.value = true
}

const handleEditTraining = (row) => {
  activeSubTab.value = 'training'
  subDialogTitle.value = getSubDialogTitle('edit')
  subForm.value = { ...row }
  subDialogVisible.value = true
}

const handleDeleteTraining = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该培训记录吗？', '提示', { type: 'warning' })
    const res = await personnelApi.trainingRecordDelete(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      await fetchSubList('trainingRecord')
    }
  } catch (e) {
    if (e !== 'cancel') {
      console.error(e)
    }
  }
}

const handleAddAssessment = () => {
  activeSubTab.value = 'assessment'
  subDialogTitle.value = getSubDialogTitle('add')
  subForm.value = initSubForm()
  subForm.value.personnelId = personnelForm.value.id
  subDialogVisible.value = true
}

const handleEditAssessment = (row) => {
  activeSubTab.value = 'assessment'
  subDialogTitle.value = getSubDialogTitle('edit')
  subForm.value = { ...row }
  subDialogVisible.value = true
}

const handleDeleteAssessment = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该考核记录吗？', '提示', { type: 'warning' })
    const res = await personnelApi.assessmentDelete(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      await fetchSubList('assessment')
    }
  } catch (e) {
    if (e !== 'cancel') {
      console.error(e)
    }
  }
}

const handleAddCertificate = () => {
  activeSubTab.value = 'certificate'
  subDialogTitle.value = getSubDialogTitle('add')
  subForm.value = initSubForm()
  subForm.value.personnelId = personnelForm.value.id
  subDialogVisible.value = true
}

const handleEditCertificate = (row) => {
  activeSubTab.value = 'certificate'
  subDialogTitle.value = getSubDialogTitle('edit')
  subForm.value = { ...row }
  subDialogVisible.value = true
}

const handleDeleteCertificate = async (row) => {
  try {
    await ElMessageBox.confirm('确定要删除该上岗证吗？', '提示', { type: 'warning' })
    const res = await personnelApi.certificateDelete(row.id)
    if (res.code === 200) {
      ElMessage.success('删除成功')
      await fetchSubList('certificate')
      fetchStats()
    }
  } catch (e) {
    if (e !== 'cancel') {
      console.error(e)
    }
  }
}

const apiMap = {
  education: { save: 'educationSave', update: 'educationUpdate', listKey: 'educationList', listApi: 'educationList' },
  title: { save: 'titleSave', update: 'titleUpdate', listKey: 'titleList', listApi: 'titleList' },
  authorization: { save: 'authorizationSave', update: 'authorizationUpdate', listKey: 'authorizationList', listApi: 'authorizationList' },
  training: { save: 'trainingRecordSave', update: 'trainingRecordUpdate', listKey: 'trainingRecordList', listApi: 'trainingRecordList' },
  assessment: { save: 'assessmentSave', update: 'assessmentUpdate', listKey: 'assessmentList', listApi: 'assessmentList' },
  certificate: { save: 'certificateSave', update: 'certificateUpdate', listKey: 'certificateList', listApi: 'certificateList' }
}

const fetchSubList = async (subTab) => {
  const tab = subTab || activeSubTab.value
  const config = apiMap[tab]
  if (!config || !personnelForm.value.id) return
  try {
    const res = await personnelApi[config.listApi](personnelForm.value.id)
    if (res.code === 200) {
      detailData.value[config.listKey] = res.data
    }
  } catch (e) {
    console.error(e)
  }
}

const handleSubSubmit = async () => {
  if (!subFormRef.value) return
  try {
    await subFormRef.value.validate()
    const config = apiMap[activeSubTab.value]
    if (!config) return
    if (subForm.value.id) {
      const res = await personnelApi[config.update](subForm.value)
      if (res.code === 200) {
        ElMessage.success('更新成功')
      }
    } else {
      const res = await personnelApi[config.save](subForm.value)
      if (res.code === 200) {
        ElMessage.success('新增成功')
      }
    }
    subDialogVisible.value = false
    await fetchSubList()
    if (activeSubTab.value === 'certificate') {
      fetchStats()
    }
  } catch (e) {
    console.error(e)
  }
}

onMounted(() => {
  fetchStats()
  fetchList()
})
</script>

<style scoped>
.page-container {
  padding: 16px;
}

.page-header {
  margin-bottom: 16px;
}

.page-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 4px;
}

.page-desc {
  font-size: 13px;
  color: #909399;
}

.stats-row {
  margin-bottom: 16px;
}

.stat-card {
  border-radius: 8px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 48px;
  height: 48px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
}

.stat-card.total .stat-icon {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
}

.stat-card.valid .stat-icon {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  color: #fff;
}

.stat-card.warning .stat-icon {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  color: #fff;
}

.stat-card.expired .stat-icon {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
  color: #fff;
}

.stat-label {
  font-size: 13px;
  color: #909399;
  margin-bottom: 4px;
}

.stat-value {
  font-size: 24px;
  font-weight: 600;
  color: #303133;
}

.toolbar {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  align-items: center;
}

.sub-toolbar {
  display: flex;
  gap: 8px;
  margin-bottom: 8px;
}
</style>
